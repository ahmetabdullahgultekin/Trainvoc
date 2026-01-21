# Phase 7: Dictionary Enrichment - Implementation Plan
## Comprehensive Technical Specification

**Status:** Planning Phase
**Estimated Effort:** 8-12 hours (developer time)
**Priority:** Medium-High
**Dependencies:** External APIs, Network layer, Database migrations
**Date:** January 21, 2026

---

## Executive Summary

Phase 7 aims to enrich the dictionary feature with real linguistic data from external APIs, replacing mock/placeholder data with accurate IPA pronunciations, part of speech information, example sentences, and synonyms. This significantly enhances the learning experience.

### Current State (Phase 6 Complete)
- ✅ Part of speech field added to Word model
- ✅ Database ready for enrichment (v15)
- 🟡 IPA, examples, synonyms all returning mock data

### Target State (Phase 7)
- ✅ IPA pronunciation from dictionary API
- ✅ Part of speech auto-populated
- ✅ Example sentences from real corpus
- ✅ Synonyms from thesaurus API
- ✅ Network layer with caching
- ✅ Offline fallback handling

---

## 📊 Current TODOs Analysis

### 1. IPA Pronunciation (WordDetailScreen.kt:1004)
```kotlin
private fun getIPA(word: String): String {
    // TODO: Implement actual IPA lookup from dictionary API
    return "/wɜːrd/" // Mock IPA
}
```

**Current Impact:** Users see fake IPA pronunciation
**Complexity:** Medium (requires API integration)

---

### 2. Part of Speech Detection (WordDetailScreen.kt:1017)
```kotlin
private fun getPartOfSpeech(word: String): String {
    // TODO: Implement proper part of speech detection
    return "noun" // Mock part of speech
}
```

**Current Impact:** Always shows "noun"
**Complexity:** Low (field exists, just needs API data)

---

### 3. Example Sentences (WordDetailScreen.kt:1030)
```kotlin
private fun getExampleSentences(word: String): List<String> {
    // TODO: Implement actual examples from database
    return listOf(
        "This is an example sentence.",
        "Here's another example."
    )
}
```

**Current Impact:** Generic placeholder examples
**Complexity:** Medium-High (needs corpus data)

---

### 4. Synonym Lookup (WordDetailScreen.kt:1042)
```kotlin
private fun getSynonyms(word: String): List<String> {
    // TODO: Implement actual synonym lookup from thesaurus API
    return listOf("word", "term", "expression")
}
```

**Current Impact:** Generic synonyms
**Complexity:** Medium (thesaurus API needed)

---

## 🏗️ Architecture Decisions Required

### Decision 1: Network Layer Choice

#### Option A: Retrofit (Recommended)
```kotlin
// build.gradle.kts
dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
}
```

**✅ Pros:**
- Industry standard
- Type-safe
- Easy error handling
- Good documentation
- Works well with Hilt

**❌ Cons:**
- Adds ~2MB to APK size
- Learning curve if team unfamiliar

---

#### Option B: Ktor Client
```kotlin
// build.gradle.kts
dependencies {
    implementation("io.ktor:ktor-client-android:2.3.0")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.0")
}
```

**✅ Pros:**
- Kotlin-native
- Smaller APK footprint
- Coroutines-first
- Modern API

**❌ Cons:**
- Less mature than Retrofit
- Fewer Stack Overflow answers

---

**Recommendation:** **Retrofit** for stability and community support

---

### Decision 2: API Provider Choice

#### Option A: Free Dictionary API (Recommended for MVP)
**URL:** `https://api.dictionaryapi.dev/api/v2/entries/en/{word}`

**Example Response:**
```json
{
  "word": "hello",
  "phonetics": [
    {"text": "/həˈloʊ/", "audio": "https://..."}
  ],
  "meanings": [
    {
      "partOfSpeech": "noun",
      "definitions": [
        {
          "definition": "A greeting",
          "example": "She gave a cheerful hello"
        }
      ],
      "synonyms": ["greeting", "salutation"]
    }
  ]
}
```

**✅ Pros:**
- Completely FREE
- No API key required
- Good coverage (English)
- Includes audio URLs
- Returns IPA, part of speech, definitions, examples, synonyms

**❌ Cons:**
- Rate limit: ~450 requests/5 minutes (reasonable)
- No Turkish support
- Limited to English words
- No guaranteed uptime SLA

**Cost:** $0/month
**Rate Limit:** 450 requests per 5 minutes
**Coverage:** English only

---

#### Option B: WordsAPI (Premium)
**URL:** `https://wordsapi.com/`

**✅ Pros:**
- Professional API
- 2,500 requests/day free tier
- Comprehensive data
- Good documentation
- Reliable uptime

**❌ Cons:**
- Requires API key
- Paid after 2,500/day
- $5-10/month for hobby tier

**Cost:** Free (2,500/day) → $10/month (25,000/day)

---

#### Option C: Merriam-Webster API
**URL:** `https://dictionaryapi.com/`

**✅ Pros:**
- Authoritative source
- Free tier available
- 1,000 requests/day
- Professional quality

**❌ Cons:**
- Requires registration
- Complex response format
- Rate limits strict

**Cost:** Free (1,000/day)

---

**Recommendation for Phase 7:**
1. **Primary:** Free Dictionary API (no key needed)
2. **Fallback:** Local database cache
3. **Future:** Add WordsAPI for premium users

---

### Decision 3: Caching Strategy

#### Approach A: Room Database Cache (Recommended)
```kotlin
@Entity(tableName = "api_cache")
data class ApiCache(
    @PrimaryKey val key: String,
    val data: String,
    val timestamp: Long,
    val expirySeconds: Int = 604800 // 7 days
)
```

**✅ Pros:**
- Offline support
- Reduces API calls
- Fast lookups
- Already using Room

**❌ Cons:**
- Increases database size
- Need cleanup strategy

---

#### Approach B: In-Memory Cache (Supplement)
```kotlin
class DictionaryCache {
    private val cache = LruCache<String, DictionaryData>(maxSize = 50)
}
```

**✅ Pros:**
- Very fast
- No disk I/O
- Simple implementation

**❌ Cons:**
- Lost on app restart
- Memory usage

---

**Recommendation:** **Hybrid approach** - In-memory + Room cache

---

## 📐 Database Schema Changes

### Migration 15→16: API Cache Table

```sql
CREATE TABLE IF NOT EXISTS api_cache (
    cache_key TEXT PRIMARY KEY NOT NULL,
    word_id TEXT NOT NULL,
    data_type TEXT NOT NULL, -- 'ipa', 'examples', 'synonyms'
    data_json TEXT NOT NULL,
    timestamp INTEGER NOT NULL,
    expiry_seconds INTEGER NOT NULL DEFAULT 604800,
    FOREIGN KEY(word_id) REFERENCES words(word) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS index_api_cache_word_id ON api_cache(word_id);
CREATE INDEX IF NOT EXISTS index_api_cache_data_type ON api_cache(data_type);
CREATE INDEX IF NOT EXISTS index_api_cache_timestamp ON api_cache(timestamp);
```

**Purpose:**
- Store API responses for offline use
- Reduce redundant API calls
- Enable background sync

---

### Migration 16→17: Synonyms Table

```sql
CREATE TABLE IF NOT EXISTS synonyms (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    word_id TEXT NOT NULL,
    synonym TEXT NOT NULL,
    relevance_score REAL DEFAULT 1.0,
    source TEXT, -- 'api', 'manual', 'computed'
    FOREIGN KEY(word_id) REFERENCES words(word) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS index_synonyms_word_id ON synonyms(word_id);
CREATE UNIQUE INDEX IF NOT EXISTS index_synonyms_unique ON synonyms(word_id, synonym);
```

**Purpose:**
- Store multiple synonyms per word
- Track source for data quality
- Enable manual synonym additions

---

### Migration 17→18: Example Sentences Table

```sql
CREATE TABLE IF NOT EXISTS example_sentences (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    word_id TEXT NOT NULL,
    sentence TEXT NOT NULL,
    source TEXT, -- 'api', 'corpus', 'manual'
    difficulty_level TEXT, -- 'A1', 'A2', 'B1', etc.
    usage_count INTEGER DEFAULT 0,
    FOREIGN KEY(word_id) REFERENCES words(word) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS index_examples_word_id ON example_sentences(word_id);
CREATE INDEX IF NOT EXISTS index_examples_difficulty ON example_sentences(difficulty_level);
```

**Purpose:**
- Store real example sentences
- Filter by difficulty level
- Track usage for learning

---

## 🔨 Implementation Steps

### Step 1: Setup Network Layer (2-3 hours)

**1.1 Add Dependencies**
```kotlin
// app/build.gradle.kts
dependencies {
    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Coroutines for async
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

**1.2 Create API Service**
```kotlin
// api/DictionaryApiService.kt
interface DictionaryApiService {
    @GET("entries/en/{word}")
    suspend fun getDictionaryEntry(
        @Path("word") word: String
    ): Response<List<DictionaryApiResponse>>
}
```

**1.3 Setup Retrofit Builder**
```kotlin
// di/NetworkModule.kt
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideDictionaryApi(client: OkHttpClient): DictionaryApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.dictionaryapi.dev/api/v2/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DictionaryApiService::class.java)
    }
}
```

---

### Step 2: Create Data Models (1 hour)

```kotlin
// api/models/DictionaryApiResponse.kt
data class DictionaryApiResponse(
    val word: String,
    val phonetics: List<Phonetic>?,
    val meanings: List<Meaning>?
)

data class Phonetic(
    val text: String?,
    val audio: String?
)

data class Meaning(
    val partOfSpeech: String,
    val definitions: List<Definition>,
    val synonyms: List<String>?
)

data class Definition(
    val definition: String,
    val example: String?
)
```

---

### Step 3: Implement Repository Layer (2-3 hours)

```kotlin
// repository/DictionaryEnrichmentRepository.kt
class DictionaryEnrichmentRepository @Inject constructor(
    private val dictionaryApi: DictionaryApiService,
    private val apiCacheDao: ApiCacheDao,
    private val wordDao: WordDao,
    private val synonymDao: SynonymDao,
    private val examplesDao: ExampleSentenceDao
) {

    suspend fun enrichWord(wordId: String): Result<EnrichedWordData> {
        return try {
            // Check cache first
            val cached = apiCacheDao.getCachedData(wordId, "dictionary")
            if (cached != null && !cached.isExpired()) {
                return Result.success(parseEnrichedData(cached.dataJson))
            }

            // Fetch from API
            val response = dictionaryApi.getDictionaryEntry(wordId)
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!.first()
                val enriched = EnrichedWordData(
                    ipa = data.phonetics?.firstOrNull()?.text,
                    partOfSpeech = data.meanings?.firstOrNull()?.partOfSpeech,
                    examples = data.meanings?.flatMap {
                        it.definitions.mapNotNull { def -> def.example }
                    } ?: emptyList(),
                    synonyms = data.meanings?.firstOrNull()?.synonyms ?: emptyList()
                )

                // Cache the result
                cacheEnrichedData(wordId, enriched)

                // Update database
                updateWordWithEnrichedData(wordId, enriched)

                Result.success(enriched)
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun cacheEnrichedData(wordId: String, data: EnrichedWordData) {
        apiCacheDao.insert(ApiCache(
            cacheKey = "dict_$wordId",
            wordId = wordId,
            dataType = "dictionary",
            dataJson = Gson().toJson(data),
            timestamp = System.currentTimeMillis(),
            expirySeconds = 604800 // 7 days
        ))
    }
}
```

---

### Step 4: Update UI Layer (2-3 hours)

```kotlin
// ui/screen/dictionary/WordDetailScreen.kt

// Replace mock functions with real implementation
private fun getIPA(word: String): String {
    // Get from enriched data if available
    return enrichedData?.ipa ?: "Loading..." // Fallback
}

private fun getPartOfSpeech(word: String): String {
    return currentWord.partOfSpeech ?: enrichedData?.partOfSpeech ?: ""
}

private fun getExampleSentences(word: String): List<String> {
    return enrichedData?.examples ?: emptyList()
}

private fun getSynonyms(word: String): List<String> {
    return enrichedData?.synonyms ?: emptyList()
}

// Add loading state
LaunchedEffect(currentWord.word) {
    viewModel.enrichWord(currentWord.word)
}
```

---

### Step 5: Database Migrations (1 hour)

```kotlin
// Implement MIGRATION_15_16, MIGRATION_16_17, MIGRATION_17_18
// Update database version to 18
// Test migrations with existing data
```

---

### Step 6: Testing & Error Handling (1-2 hours)

**Test Cases:**
1. ✅ API call succeeds - data displayed
2. ✅ API call fails - show cached data
3. ✅ No cache, no internet - show placeholder
4. ✅ Rate limit exceeded - show message
5. ✅ Invalid word - handle gracefully
6. ✅ Empty response - fallback to basics

---

## ⚠️ Risk Assessment

### Risk 1: API Rate Limits
**Probability:** Medium
**Impact:** High
**Mitigation:**
- Implement aggressive caching
- Only call API when user explicitly views word detail
- Show cached data immediately
- Background refresh when available

---

### Risk 2: API Availability
**Probability:** Low
**Impact:** Medium
**Mitigation:**
- Graceful degradation to mock data
- Offline-first architecture
- Multiple API fallbacks

---

### Risk 3: Data Quality
**Probability:** Medium
**Impact:** Medium
**Mitigation:**
- Validate API responses
- Manual curation for common words
- User reporting for errors

---

### Risk 4: APK Size Increase
**Probability:** High
**Impact:** Low
**Mitigation:**
- Use Retrofit (smaller than alternatives)
- ProGuard optimization
- Estimate: +1-2MB APK size

---

## 📋 Checklist for Phase 7

### Prerequisites
- [ ] Decide on API provider (Free Dictionary API recommended)
- [ ] Decide on network library (Retrofit recommended)
- [ ] Plan cache eviction strategy (7 days recommended)
- [ ] Design offline fallback UX

### Implementation
- [ ] Add network dependencies to build.gradle
- [ ] Create API service interface
- [ ] Setup Retrofit/Hilt module
- [ ] Create data models
- [ ] Implement repository layer
- [ ] Create database migrations (15→18)
- [ ] Update DAOs for new tables
- [ ] Update ViewModels to use real data
- [ ] Update UI to show loading states
- [ ] Replace mock functions with API calls

### Testing
- [ ] Unit tests for repository
- [ ] Network error handling tests
- [ ] Cache expiry tests
- [ ] UI loading state tests
- [ ] Manual testing with real API
- [ ] Offline mode testing

### Documentation
- [ ] Update README with API attribution
- [ ] Document rate limits
- [ ] Add API setup instructions
- [ ] Update architecture diagrams

---

## 💰 Cost Analysis

### Development Time
- Network layer: 2-3 hours
- Data models: 1 hour
- Repository: 2-3 hours
- UI updates: 2-3 hours
- Migrations: 1 hour
- Testing: 1-2 hours
- **Total: 9-13 hours**

### Infrastructure Costs
- API: $0/month (Free Dictionary API)
- Hosting: $0 (client-side only)
- **Total: $0/month**

### Maintenance
- API monitoring: 1 hour/month
- Cache cleanup: Automated
- Bug fixes: As needed

---

## 🚀 Alternative: Lightweight Phase 7

If 9-13 hours is too much, here's a **minimal viable implementation**:

### Lightweight Approach (3-4 hours)
1. ✅ Use Free Dictionary API (no key)
2. ✅ In-memory cache only (no new DB tables)
3. ✅ Update only IPA and part of speech
4. ❌ Skip examples table (keep simple list)
5. ❌ Skip synonyms table (keep simple list)
6. ✅ Basic error handling only

**Result:** Functional enrichment with 60% less effort

---

## 📊 Success Metrics

### Technical Metrics
- API success rate > 95%
- Cache hit rate > 70%
- Average response time < 500ms
- App size increase < 2MB

### User Experience Metrics
- Enriched words show real IPA
- Part of speech auto-populated
- Examples are contextual
- Synonyms are relevant

---

## 🔮 Future Enhancements (Beyond Phase 7)

### Phase 8: Advanced Features
- Audio pronunciation playback
- Etymology information
- Collocations (word pairs)
- Frequency rankings
- Regional variations (US vs UK)

### Phase 9: User Contributions
- Report incorrect data
- Suggest better examples
- Add custom synonyms
- Community-sourced content

---

## 📞 Support & Resources

### API Documentation
- Free Dictionary API: https://dictionaryapi.dev/
- WordsAPI: https://www.wordsapi.com/docs
- Merriam-Webster: https://dictionaryapi.com/products/api-collegiate-dictionary

### Code Examples
- Retrofit setup: Official docs
- Room migrations: Android developers guide
- Error handling: Kotlin Result type

---

## ✅ Recommendation

**For Trainvoc, I recommend:**

1. **Start with Free Dictionary API** - No cost, good data
2. **Use Retrofit** - Industry standard, reliable
3. **Implement full Phase 7** - Worth the 9-13 hour investment
4. **Add hybrid caching** - Best user experience
5. **Plan for offline-first** - Keep app functional

**Estimated ROI:**
- Development: 10 hours
- Impact: Significantly better learning experience
- Cost: $0/month
- Risk: Low (graceful degradation)

---

**Status:** Ready for implementation
**Next Step:** Get approval and start with network layer setup

---

**END OF PHASE 7 PLAN**
