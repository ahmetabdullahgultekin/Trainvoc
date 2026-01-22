PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE android_metadata
(
    locale TEXT
);
INSERT INTO android_metadata
VALUES ('tr_TR');
CREATE TABLE ` words `
(
    `
    word
    `
    TEXT
    NOT
    NULL,
    `
    meaning
    `
    TEXT
    NOT
    NULL,
    `
    level
    `
    TEXT,
    `
    last_reviewed
    `
    INTEGER,
    `
    stat_id
    `
    INTEGER
    NOT
    NULL,
    `
    seconds_spent
    `
    INTEGER
    NOT
    NULL,
    PRIMARY
    KEY
(
    `
    word
    `
));
INSERT INTO words
VALUES ('A/an', 'Bir', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('About', 'Hakkında', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Above', 'Üstünde', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Across', 'Karşısında', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Action', 'Aksiyon', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Activity', 'Aktivite', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Actor', 'Aktör', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Actress', 'Aktris', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Add', 'Eklemek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Address', 'Adres', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Adult', 'Yetişkin', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Advice', 'Tavsiye', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Afraid', 'Korkmuş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('After', 'Sonrasında', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Afternoon', 'Öğleden sonra', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Again', 'Tekrar', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Age', 'Yaş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ago', 'Evvel', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Agree', 'Kabul etmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Air', 'Hava', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Airport', 'Havalimanı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('All', 'Tümü', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Also', 'Ayrıca', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Always', 'Her zaman', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Amazing', 'İnanılmaz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('And', 'Ve', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Angry', 'Sinirli', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Animal', 'Hayvan', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Another', 'Bir diğer', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Answer', 'Cevap', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Any', 'Herhangi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Anyone', 'Herhangi biri', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Anything', 'Herhangi bir şey', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Apartment', 'Daire', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Apple', 'Elma', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('April', 'Nisan', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Area', 'Alan', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Arm', 'Kol', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Around', 'Etrafında', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Arrive', 'Varmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Art', 'Sanat', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Article', 'Madde', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Artist', 'Sanatçı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('As', 'Gibi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ask', 'Sormak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('At', '-den', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('August', 'Ağustos', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Aunt', 'Hala', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Autumn', 'Sonbahar', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Away', 'Uzak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Baby', 'Bebek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Back', 'Geri', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bad', 'Kötü', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bag', 'Çanta', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ball', 'Top', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Banana', 'Muz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Band', 'Bant', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bank (money)', 'Banka (para)', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bath', 'Banyo', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bathroom', 'Banyo', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Be', 'Olmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Beach', 'Sahil', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Beautiful', 'Güzel', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Because', 'Çünkü', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Become', 'Haline gelmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bed', 'Yatak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bedroom', 'Yatak odası', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Beer', 'Bira', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Before', 'Önce', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Begin', 'Başlamak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Beginning', 'Başlangıç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Behind', 'Arka', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Believe', 'İnanmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Below', 'Altında', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Best', 'En iyi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Better', 'Daha iyi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Between', 'Arasında', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bicycle', 'Bisiklet', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Big', 'Büyük', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bike', 'Bisiklet', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bill', 'Fatura', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bird', 'Kuş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Birthday', 'Doğum günü', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Black', 'Siyah', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Blog', 'Blog', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Blonde', 'Sarışın', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Blue', 'Mavi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Boat', 'Bot', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Body', 'Vücut', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Book', 'Kitap', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Boot', 'Bot', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bored', 'Sıkılmış', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Boring', 'Sıkıcı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Born', 'Doğmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Both', 'İkisi birden', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bottle', 'Şişe', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Box', 'Kutu', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Boy', 'Erkek çocuk', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Boyfriend', 'Erkek arkadaş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bread', 'Ekmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Break', 'Kırmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Breakfast', 'Kahvaltı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bring', 'Getirmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Brother', 'Erkek kardeş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Brown', 'Kahverengi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Build', 'İnşa etmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Building', 'Bina', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bus', 'Otobüs', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Business', 'İşletme', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Busy', 'Meşgul', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('But', 'Ancak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Butter', 'Tereyağı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Buy', 'Satın almak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('By', 'İle', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bye', 'Güle güle', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cafe', 'Kafe', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cake', 'Kek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Call', 'Arama', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Camera', 'Kamera', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Can', 'Olabilmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cannot', 'Yapamamak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Capital', 'Başkent', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Car', 'Araba', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Card', 'Kart', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Career', 'Kariyer', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Carrot', 'Havuç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Carry', 'Taşımak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cat', 'Kedi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('CD', 'CD', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cent', 'Yüzyıl', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Centre', 'Merkez', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Century', 'Yüzyıl', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Chair', 'Sandalye', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Change', 'Değiştirmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Chart', 'Çizelge', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cheap', 'Ucuz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Check', 'Kontrol etmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cheese', 'Peynir', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Chicken', 'Tavuk', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Child', 'Çocuk', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Chocolate', 'Çikolata', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Choose', 'Seçmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cinema', 'Sinema', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('City', 'Şehir', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Class', 'Sınıf', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Classroom', 'Sınıf', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Clean', 'Temiz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Climb', 'Tırmanmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Clock', 'Saat', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Close', 'Kapalı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Clothes', 'Kıyafetler', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Club', 'Kulüp', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Coat', 'Kaban', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Coffee', 'Kahve', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cold', 'Soğuk', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('College', 'Kolej', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Colour', 'Renk', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Come', 'Gelmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Common', 'Yaygın', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Company', 'Şirket', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Compare', 'Karşılaştırmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Complete', 'Tamamlamak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Computer', 'Bilgisayar', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Concert', 'Konser', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Conversation', 'Konuşma', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cook', 'Aşçı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cooking', 'Yemek pişirmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cool', 'Serin', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Correct', 'Doğru', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cost', 'Maliyet', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Could', 'Olabilir', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Country', 'Ülke', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Course', 'Kurs', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cousin', 'Kuzen', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cow', 'İnek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cream', 'Krema', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Create', 'Yaratmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Culture', 'Kültür', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cup', 'Bardak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Customer', 'Müşteri', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cut', 'Kesmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dad', 'Baba', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dance', 'Dans', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dancer', 'Dansçı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dancing', 'Dans', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dangerous', 'Tehlikeli', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dark', 'Karanlık', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Date', 'Tarih', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Daughter', 'Kız çocuğu', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Day', 'Gün', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dear', 'Sayın', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('December', 'Aralık', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Decide', 'Karar vermek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Delicious', 'Lezzetli', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Describe', 'Betimlemek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Description', 'Tanım', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Design', 'Tasarım', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Desk', 'Çalışma masası', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Detail', 'Detay', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dialogue', 'Diyalog', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dictionary', 'Sözlük', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Die', 'Ölmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Diet', 'Diyet', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Difference', 'Fark', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Different', 'Farklı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Difficult', 'Zor', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dinner', 'Akşam yemeği', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dirty', 'Kirli', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Discuss', 'Tartışmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dish', 'Tabak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Do', 'Yapmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Doctor', 'Doktor', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dog', 'Köpek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dollar', 'Dolar', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Door', 'Kapı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Down', 'Aşağı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Downstairs', 'Alt katta', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Draw', 'Çizmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dress', 'Elbise', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Drink', 'İçmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Drive', 'Sürmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Driver', 'Sürücü', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('During', 'Sırasında', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('DVD', 'DVD', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Each', 'Her biri', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ear', 'Kulak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Early', 'Erken', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('East', 'Doğu', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Easy', 'Kolay', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Eat', 'Yemek yemek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Egg', 'Yumurta', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Eight', 'Sekiz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Eighteen', 'On sekiz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Eighty', 'Seksen', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Elephant', 'Fil', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Eleven', 'On bir', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Else', 'Başka', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Email', 'E -posta', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('End', 'Son', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Enjoy', 'Eğlence', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Enough', 'Yeterli', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Euro', 'Euro', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Even', 'Eşit', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Evening', 'Akşam', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Event', 'Etkinlik', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ever', 'Durmadan', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Every', 'Her', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Everybody', 'Herkes', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Everyone', 'Herkes', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Everything', 'Her şey', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Exam', 'Sınav', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Example', 'Örnek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Excited', 'Heyecanlı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Exciting', 'Heyecan verici', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Exercise', 'Egzersiz yapmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Expensive', 'Masraflı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Explain', 'Açıklamak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Extra', 'Ekstra', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Eye', 'Göz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Face', 'Yüz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fact', 'Hakikat', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fall', 'Düşmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('False', 'YANLIŞ', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Family', 'Aile', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Famous', 'Ünlü', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fantastic', 'Fantastik', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Far', 'Uzak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Farm', 'Çiftlik', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Farmer', 'Çiftçi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fast', 'Hızlı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fat', 'Yağ', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Father', 'Baba', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Favourite', 'Favori', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('February', 'Şubat', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Feel', 'Hissetmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Feeling', 'His', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Festival', 'Festival', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Few', 'Bir kaç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fifteen', 'Onbeş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fifth', 'Beşinci', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fifty', 'Elli', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fill', 'Doldurmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Film', 'Film', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Final', 'Son', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Find', 'Bulmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fine', 'İyi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Finish', 'Sona ermek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fire', 'Ateş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('First', 'Birinci', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fish', 'Balık', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Five', 'Beş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Flat', 'Düz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Flight', 'Uçuş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Floor', 'Zemin', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Flower', 'Çiçek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fly', 'Uçmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Follow', 'Takip etmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Food', 'Yiyecek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Foot', 'Ayak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Football', 'Futbol', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('For', 'İçin', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Forget', 'Unutmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Form', 'Biçim', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Forty', 'Kırk', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Four', 'Dört', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fourteen', 'On dört', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fourth', 'Dördüncü', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Free', 'Özgür', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Friday', 'Cuma', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Friend', 'Arkadaş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Friendly', 'Arkadaşça', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('From', 'İtibaren', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Front', 'Ön', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fruit', 'Meyve', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Full', 'Tam dolu', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fun', 'Eğlence', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Funny', 'Eğlenceli', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Future', 'Gelecek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Game', 'Oyun', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Garden', 'Bahçe', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Geography', 'Coğrafya', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Get', 'Elde etmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Girl', 'Kız', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Girlfriend', 'Kız arkadaşı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Give', 'Vermek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Glass', 'Bardak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Go', 'Gitmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Good', 'İyi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Goodbye', 'Güle güle', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Grandfather', 'Büyükbaba', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Grandmother', 'Büyükanne', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Grandparent', 'Büyükanne ve büyükbaba', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Great', 'Harika', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Green', 'Yeşil', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Grey', 'Gri', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Group', 'Grup', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Grow', 'Büyümek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Guess', 'Tahmin etmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Guitar', 'Gitar', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Gym', 'Spor salonu', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hair', 'Saç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Half', 'Yarım', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hand', 'El', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Happen', 'Olmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Happy', 'Mutlu', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hard', 'Zor', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hat', 'Şapka', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hate', 'Nefret', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Have', 'Sahip olmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Have to', 'Zorunda', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('He', 'O', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Head', 'KAFA', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Health', 'Sağlık', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Healthy', 'Sağlıklı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hear', 'Duymak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hello', 'Merhaba', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Help', 'Yardım', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Her', 'O', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Here', 'Burada', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hey', 'Hey', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hi', 'MERHABA', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('High', 'Yüksek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Him', 'O', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('His', 'Onun', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('History', 'Tarih', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hobby', 'Hobi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Holiday', 'Tatil', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Home', 'Ev', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Homework', 'Ev ödevi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hope', 'Umut', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Horse', 'Atış', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hospital', 'Hastane', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hot', 'Sıcak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hotel', 'Otel', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hour', 'Saat', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('House', 'Ev', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('How', 'Nasıl', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('However', 'Yine de', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hundred', 'Yüz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hungry', 'Aç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Husband', 'Koca', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ice', 'Buz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ice cream', 'Dondurma', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Idea', 'Fikir', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('If', 'Eğer', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Imagine', 'Hayal etmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Important', 'Önemli', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Improve', 'Geliştirmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('In', 'İçinde', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Include', 'Katmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Information', 'Bilgi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Interest', 'Faiz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Interested', 'İlgili', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Interesting', 'İlginç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Internet', 'İnternet', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Interview', 'Röportaj', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Into', 'İçine', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Introduce', 'Tanıtmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Island', 'Ada', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('It', 'BT', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Its', 'Onun', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Jacket', 'Ceket', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('January', 'Ocak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Jeans', 'Kot', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Job', 'İş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Join', 'Katılmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Journey', 'Seyahat', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Juice', 'Meyve suyu', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('July', 'Temmuz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('June', 'Haziran', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Just', 'Sadece', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Keep', 'Kale', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Key', 'Anahtar', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Kilometre', 'Kilometre', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Kind (type)', 'Kibar (tip)', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Kitchen', 'Mutfak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Know', 'Bilmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Land', 'Kara', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Language', 'Dil', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Large', 'Büyük', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Last (final)', 'Son (final)', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Late', 'Geç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Later', 'Daha sonra', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Laugh', 'Gülmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Learn', 'Öğrenmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Leave', 'Ayrılmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Left', 'Sol', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Leg', 'Bacak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lesson', 'Ders', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Let', 'İzin vermek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Letter', 'Mektup', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Library', 'Kütüphane', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lie', 'Yalan', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Life', 'Hayat', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Light (from the sun/a lamp)', 'Işık (güneşten/bir lambadan)', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Like (similar)', 'Gibi (benzer)', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Like (find sb/sth pleasant)', 'Gibi (sb/sth pleasant''ı bul)', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Line', 'Astar', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lion', 'Aslan', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('List', 'Liste', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Listen', 'Dinlemek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Little', 'Biraz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Live', 'Canlı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Local', 'Yerel', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Long', 'Uzun', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Look', 'Bakmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lose', 'Kaybetmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lot', 'Pay', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Love', 'Aşk', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lunch', 'Öğle yemeği', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Machine', 'Makine', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Magazine', 'Dergi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Main', 'Ana', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Make', 'Yapmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Man', 'Adam', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Many', 'Birçok', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Map', 'Harita', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('March', 'Mart', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Market', 'Pazar', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Married', 'Evli', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Match (contest)', 'Maç (yarışma)', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('May', 'Mayıs', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Maybe', 'Belki', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Me', 'Ben', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Meal', 'Yemek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mean', 'Anlam', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Meaning', 'Anlam', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Meat', 'Et', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Meet', 'Tanışmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Meeting', 'Toplantı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Member', 'Üye', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Menu', 'Menü', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Message', 'Mesaj', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Metre', 'Metre', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Midnight', 'Gece yarısı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mile', 'Mil', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Milk', 'Süt', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Million', 'Milyon', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Minute', 'Dakika', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Miss', 'Kayıp', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mistake', 'Hata', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Model', 'Model', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Modern', 'Modern', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Moment', 'An', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Monday', 'Pazartesi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Money', 'Para', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Month', 'Ay', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('More', 'Daha', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Morning', 'Sabah', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Most', 'En', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mother', 'Anne', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mountain', 'Dağ', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mouse', 'Fare', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mouth', 'Ağız', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Move', 'Taşınmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Movie', 'Film', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Much', 'Fazla', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mum', 'Anne', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Museum', 'Müze', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Music', 'Müzik', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Must', 'Mutlak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('My', 'Benim', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Name', 'İsim', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Natural', 'Doğal', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Near', 'Yakın', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Need', 'İhtiyaç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Negative', 'Negatif', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Neighbour', 'Komşu', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Never', 'Asla', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('New', 'Yeni', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('News', 'Haberler', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Newspaper', 'Gazete', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Next', 'Sonraki', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Next to', 'Yanında', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nice', 'Güzel', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Night', 'Gece', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nine', 'Dokuz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nineteen', 'On dokuz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ninety', 'Doksan', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('No', 'HAYIR', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('No one', 'Hiç kimse', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nobody', 'Hiç kimse', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('North', 'Kuzey', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nose', 'Burun', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Not', 'Olumsuz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Note', 'Not', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nothing', 'Hiçbir şey', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('November', 'Kasım', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Now', 'Şimdi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Number', 'Sayı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nurse', 'Hemşire', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Object', 'Nesne', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('O''clock', 'Saat', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('October', 'Ekim', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Of', 'İle ilgili', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Off', 'Kapalı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Office', 'Ofis', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Often', 'Sıklıkla', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Oh', 'Ah', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('OK', 'TAMAM', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Old', 'Eskimiş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('On', 'Açık', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Once', 'Bir kere', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('One', 'Bir', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Onion', 'Soğan', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Online', 'Çevrimiçi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Only', 'Sadece', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Open', 'Açık', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Opinion', 'Fikir', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Opposite', 'Zıt', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Or', 'Veya', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Orange', 'Turuncu', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Order', 'Emir', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Other', 'Diğer', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Our', 'Bizim', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Out', 'Dışarı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Outside', 'Dıştan', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Over', 'Üzerinde', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Own', 'Sahip olmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Page', 'Sayfa', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Paint', 'Boyamak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Painting', 'Tablo', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pair', 'Çift', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Paper', 'Kağıt', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Paragraph', 'Paragraf', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Parent', 'Ebeveyn', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Park', 'Park', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Part', 'Parça', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Partner', 'Ortak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Party', 'Parti', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Passport', 'Pasaport', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Past', 'Geçmiş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pay', 'Ödemek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pen', 'Dolma kalem', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pencil', 'Kalem', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('People', 'İnsanlar', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pepper', 'Biber', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Perfect', 'Mükemmel', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Period', 'Dönem', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Person', 'Kişi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Personal', 'Kişisel', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Phone', 'Telefon', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Photo', 'Fotoğraf', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Photograph', 'Fotoğraf', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Phrase', 'İfade etmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Piano', 'Piyano', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Picture', 'Resim', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Piece', 'Parça', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pig', 'Domuz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pink', 'Pembe', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Place', 'Yer', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Plan', 'Planlamak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Plane', 'Uçak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Plant', 'Bitki', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Play', 'Oynamak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Player', 'Oyuncu', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Please', 'Lütfen', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Point', 'Nokta', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Police', 'Polis', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Policeman', 'Polis', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pool', 'Havuz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Poor', 'Fakir', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Popular', 'Popüler', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Positive', 'Olumlu', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Possible', 'Olası', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Post', 'Postalamak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Potato', 'Patates', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pound', 'Pound', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Practice', 'Pratik', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Practise', 'Pratik', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prefer', 'Tercih etmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prepare', 'Hazırlanmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Present', 'Sunmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pretty', 'Tatlı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Price', 'Fiyat', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Probably', 'Muhtemelen', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Problem', 'Sorun', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Product', 'Ürün', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Programme', 'Program', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Project', 'Proje', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Purple', 'Mor', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Put', 'Koymak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Quarter', 'Çeyrek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Question', 'Soru', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Quick', 'Hızlı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Quickly', 'Hızlıca', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Quiet', 'Sessizlik', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Quite', 'Epeyce', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Radio', 'Radyo', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rain', 'Yağmur', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Read', 'Okumak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reader', 'Okuyucu', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reading', 'Okuma', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ready', 'Hazır', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Real', 'Gerçek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Really', 'Gerçekten', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reason', 'Sebep', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Red', 'Kırmızı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Relax', 'Rahatlamak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Remember', 'Unutma', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Repeat', 'Tekrarlamak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Report', 'Rapor', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Restaurant', 'Restoran', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Result', 'Sonuç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Return', 'Geri dönmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rice', 'Pirinç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rich', 'Zengin', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ride', 'Sürmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Right', 'Sağ', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('River', 'Nehir', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Road', 'Yol', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Room', 'Oda', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Routine', 'Rutin', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rule', 'Kural', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Run', 'Koşmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sad', 'Üzücü', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Salad', 'Salata', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Salt', 'Tuz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Same', 'Aynı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sandwich', 'Sandviç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Saturday', 'Cumartesi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Say', 'Söylemek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('School', 'Okul', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Science', 'Bilim', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Scientist', 'Bilim adamı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sea', 'Deniz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Second', 'İkinci', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Section', 'Bölüm', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('See', 'Görmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sell', 'Satmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Send', 'Göndermek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sentence', 'Cümle', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('September', 'Eylül', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Seven', 'Yedi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Seventeen', 'On yedi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Seventy', 'Yetmiş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Share', 'Paylaşmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('She', 'O', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sheep', 'Koyun', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shirt', 'Gömlek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shoe', 'Ayakkabı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shop', 'Mağaza', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shopping', 'Alışveriş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Short', 'Kısa', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Should', 'Zorunlu', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Show', 'Göstermek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shower', 'Duş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sick', 'Hasta', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Similar', 'Benzer', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sing', 'Şarkı söylemek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Singer', 'Şarkıcı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sister', 'Kız kardeş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sit', 'Oturmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Situation', 'Durum', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Six', 'Altı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sixteen', 'On altı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sixty', 'Altmış', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Skill', 'Yetenek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Skirt', 'Etek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sleep', 'Uyumak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Slow', 'Yavaş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Small', 'Küçük', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Snake', 'Yılan', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Snow', 'Kar', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('So', 'Bu yüzden', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Some', 'Bazı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Somebody', 'Biri', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Someone', 'Birisi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Something', 'Bir şey', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sometimes', 'Bazen', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Son', 'Oğlu', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Song', 'Şarkı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Soon', 'Yakında', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sorry', 'Üzgünüm', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sound', 'Ses', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Soup', 'Çorba', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('South', 'Güney', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Space', 'Uzay', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Speak', 'Konuşmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Special', 'Özel', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spell', 'Hecelemek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spelling', 'Yazım', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spend', 'Harcamak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sport', 'Spor', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spring', 'Bahar', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stand', 'Durmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Star', 'Yıldız', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Start', 'Başlangıç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Statement', 'İfade', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Station', 'İstasyon', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stay', 'Kalmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Still', 'Hala', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stop', 'Durmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Story', 'Hikaye', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Street', 'Sokak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Strong', 'Güçlü', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Student', 'Öğrenci', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Study', 'Çalışmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Style', 'Stil', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Subject', 'Ders', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Success', 'Başarı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sugar', 'Şeker', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Summer', 'Yaz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sun', 'Güneş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sunday', 'Pazar', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Supermarket', 'Süpermarket', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sure', 'Elbette', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sweater', 'Kazak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Swim', 'Yüzmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Swimming', 'Yüzme', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Table', 'Masa', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Take', 'Almak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Talk', 'Konuşmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tall', 'Uzun', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Taxi', 'Taksi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tea', 'Çay', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Teach', 'Öğretmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Teacher', 'Öğretmen', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Team', 'Takım', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Teenager', 'Genç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Telephone', 'Telefon', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Television', 'Televizyon', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tell', 'Söylemek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ten', 'On', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tennis', 'Tenis', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Terrible', 'Korkunç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Test', 'Test', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Text', 'Metin', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Than', 'Hariç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Thank', 'Teşekkür etmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Thanks', 'Teşekkürler', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('That', 'O', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('The', '.', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Theatre', 'Tiyatro', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Their', 'Onların', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Them', 'Onlara', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Then', 'Daha sonra', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('There', 'Orası', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('They', 'Onlar', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Thing', 'Şey', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Think', 'Düşünmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Third', 'Üçüncü', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Thirsty', 'Susuz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Thirteen', 'On üç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Thirty', 'Otuz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('This', 'Bu', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Thousand', 'Bin', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Three', 'Üç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Through', 'Başından sonuna kadar', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Thursday', 'Perşembe', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ticket', 'Bilet', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Time', 'Zaman', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tired', 'Yorgun', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Title', 'Başlık', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('To', 'İle', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Today', 'Bugün', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Together', 'Birlikte', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Toilet', 'Tuvalet', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tomato', 'Domates', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tomorrow', 'Yarın', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tonight', 'Bu akşam', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Too', 'Fazla', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tooth', 'Diş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Topic', 'Başlık', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tourist', 'Turist', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Town', 'Şehir', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Traffic', 'Trafik', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Train', 'Tren', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Travel', 'Seyahat', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tree', 'Ağaç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Trip', 'Seyahat', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Trousers', 'Pantolonlar', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('True', 'Gerçek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Try', 'Denemek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('T-shirt', 'Tişört', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tuesday', 'Salı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Turn', 'Dönüş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('TV', 'TV', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Twelve', 'On iki', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Twenty', 'Yirmi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Twice', 'İki kere', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Two', 'İki', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Type', 'Tip', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Umbrella', 'Şemsiye', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Uncle', 'Amca', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Under', 'Altında', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Understand', 'Anlamak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('University', 'Üniversite', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Until', 'Değin', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Up', 'Yukarı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Upstairs', 'Üst katta', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Us', 'Biz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Use', 'Kullanmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Useful', 'Kullanışlı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Usually', 'Genellikle', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Vacation', 'Tatil', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Vegetable', 'Sebze', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Very', 'Çok', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Video', 'Video', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Village', 'Köy', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Visit', 'Ziyaret etmek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Visitor', 'Ziyaretçi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Wait', 'Beklemek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Waiter', 'Garson', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Wake', 'Uyandırmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Walk', 'Yürümek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Wall', 'Duvar', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Want', 'İstek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Warm', 'Ilık', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Wash', 'Yıkamak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Watch', 'Kol saati', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Water', 'Su', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Way', 'Yol', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('We', 'Biz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Wear', 'Giymek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Weather', 'Hava durumu', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Website', 'Web sitesi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Wednesday', 'Çarşamba', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Week', 'Hafta', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Weekend', 'Hafta sonu', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Welcome', 'Hoş geldin', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Well', 'Peki', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('West', 'Batı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('What', 'Ne', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('When', 'Ne zaman', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Where', 'Neresi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Which', 'Hangi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('White', 'Beyaz', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Who', 'DSÖ', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Why', 'Neden', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Wife', 'Eş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Will', 'İrade', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Win', 'Kazanç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Window', 'Pencere', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Wine', 'Şarap', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Winter', 'Kış', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('With', 'İle', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Without', 'Olmadan', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Woman', 'Kadın', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Wonderful', 'Müthiş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Word', 'Kelime', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Work', 'İş', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Worker', 'İşçi', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('World', 'Dünya', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Would', 'İstemek', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Write', 'Yazmak', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Writer', 'Yazar', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Writing', 'Yazma', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Wrong', 'Yanlış', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Yeah', 'Evet', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Year', 'Yıl', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Yellow', 'Sarı', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Yes', 'Evet', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Yesterday', 'Dün', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('You', 'Sen', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Young', 'Genç', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Your', 'Senin', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Yourself', 'Kendin', 'A1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ability', 'Yetenek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Able', 'Hünerli', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Abroad', 'Yurt dışı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Accept', 'Kabul etmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Accident', 'Kaza', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('According to', 'Buna göre', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Achieve', 'Başarmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Act', 'Davranmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Active', 'Aktif', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Actually', 'Aslında', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Advantage', 'Avantaj', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Adventure', 'Macera', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Advertise', 'İlan etmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Advertisement', 'Reklamcılık', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Advertising', 'Reklam', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Affect', 'Etkilemek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Against', 'Aykırı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ah', 'Ah', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Airline', 'Havayolu', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Alive', 'Canlı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('All right', 'Elbette', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Allow', 'İzin vermek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Almost', 'Neredeyse', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Alone', 'Yalnız', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Along', 'Birlikte', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Already', 'Çoktan', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Alternative', 'Alternatif', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Although', 'Rağmen', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Among', 'Arasında', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Amount', 'Miktar', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ancient', 'Antik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ankle', 'Bilek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Anybody', 'Kimse', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Any more', 'Artık değil', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Anyway', 'Neyse', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Anywhere', 'Herhangi bir yer', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('App', 'Uygulama', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Appear', 'Belli olmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Appearance', 'Dış görünüş', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Apply', 'Uygula', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Architect', 'Mimar', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Architecture', 'Mimarlık', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Argue', 'Tartışmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Argument', 'Argüman', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Army', 'Ordu', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Arrange', 'Düzenlemek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Arrangement', 'Ayarlama', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Asleep', 'Uyuya kalmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Assistant', 'Asistan', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Athlete', 'Atlet', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Attack', 'Saldırı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Attend', 'Katılmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Attention', 'Dikkat', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Attractive', 'Çekici', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Audience', 'Kitle', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Author', 'Yazar', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Available', 'Mevcut', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Average', 'Ortalama', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Avoid', 'Kaçınmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Award', 'Ödül', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Awful', 'Berbat', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Background', 'Arka plan', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Badly', 'Kötü', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bar', 'Çubuk', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Baseball', 'Beyzbol', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Based', 'Temelli', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Basketball', 'Basketbol', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bean', 'Fasulye', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bear (animal)', 'Ayı (hayvan)', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Beat', 'Vurmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Beef', 'Biftek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Behave', 'Davranmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Behaviour', 'Davranış', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Belong', 'Ait olmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Belt', 'Kemer', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Benefit', 'Fayda', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Billion', 'Milyar', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bin', 'Çöp kutusu', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Biology', 'Biyoloji', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Birth', 'Doğum', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Biscuit', 'Bisküvi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bit', 'Biraz', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Blank', 'Boşluk', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Blood', 'Kan', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Blow', 'Üflemek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Board', 'Pano', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Boil', 'Kaynamak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bone', 'Kemik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Borrow', 'Borç almak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Boss', 'Patron', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bottom', 'Alt', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bowl', 'Tas', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Brain', 'Beyin', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bridge', 'Köprü', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bright', 'Parlak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Brilliant', 'Muhteşem', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Broken', 'Kırık', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Brush', 'Fırçalamak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Burn', 'Yakmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Businessman', 'İşadamı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Button', 'Düğme', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Camp', 'Kamp', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Camping', 'Kamp', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Care', 'Özen', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Careful', 'Dikkatli olmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Carefully', 'Dikkatlice', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Carpet', 'Halı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cartoon', 'Karikatür', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Case', 'Dava', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cash', 'Peşin', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Castle', 'Kale', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Catch', 'Yakalamak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cause', 'Neden', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Celebrate', 'Kutlamak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Celebrity', 'Ünlü', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Certain', 'Kesin', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Certainly', 'Kesinlikle', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Chance', 'Şans', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Character', 'Karakter', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Charity', 'Hayır kurumu', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Chat', 'Sohbet', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Chef', 'Şef', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Chemistry', 'Kimya', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Chip', 'Çip', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Choice', 'Seçenek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Church', 'Kilise', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cigarette', 'Sigara', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Circle', 'Daire', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Classical', 'Klasik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Clear', 'Temizlemek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Clearly', 'Açıkça', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Clever', 'Akıllı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Climate', 'İklim', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Closed', 'Kapalı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Clothing', 'Giyim', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cloud', 'Bulut', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Coach', 'Koç', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Coast', 'Sahil', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Code', 'Kodlamak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Colleague', 'İş arkadaşı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Collect', 'TOPLAMAK', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Column', 'Kolon', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Comedy', 'Komedi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Comfortable', 'Rahat', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Comment', 'Yorum', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Communicate', 'İletişim', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Community', 'Toplum', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Compete', 'Rekabet etmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Competition', 'Yarışma', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Complain', 'Şikayet etmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Completely', 'Tamamen', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Condition', 'Durum', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Conference', 'Konferans', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Connect', 'Bağlamak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Connected', 'Bağlı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Consider', 'Dikkate almak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Contain', 'İçermek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Context', 'Bağlam', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Continent', 'Kıta', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Continue', 'Devam etmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Control', 'Kontrol', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cooker', 'Ocak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Copy', 'Kopya', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Corner', 'Köşe', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Correctly', 'Doğruca', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Count', 'Saymak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Couple', 'Çift', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cover', 'Kapak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Crazy', 'Deli', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Creative', 'Yaratıcı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Credit', 'Kredi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Crime', 'Suç', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Criminal', 'Suçlu', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cross', 'Geçmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Crowd', 'Kalabalık', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Crowded', 'Kalabalık', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cry', 'Ağlamak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cupboard', 'Büfe', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Curly', 'Kıvırcık', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cycle', 'Döngü', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Daily', 'Günlük', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Danger', 'Tehlike', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Data', 'Veri', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dead', 'Ölü', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Deal', 'Anlaşmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Death', 'Ölüm', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Decision', 'Karar', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Deep', 'Derin', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Definitely', 'Kesinlikle', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Degree', 'Derece', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dentist', 'Dişçi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Department', 'Departman', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Depend', 'Bağlı olmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Desert', 'Çöl', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Designer', 'Tasarımcı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Destroy', 'Tahrip etmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Detective', 'Dedektif', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Develop', 'Geliştirmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Device', 'Cihaz', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Diary', 'Günlük', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Differently', 'Farklı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Digital', 'Dijital', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Direct', 'Yönlendirmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Direction', 'Yön', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Director', 'Müdür', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Disagree', 'Aynı fikirde olmamak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Disappear', 'Yok olmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Disaster', 'Felaket', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Discover', 'Keşfetmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Discovery', 'Keşif', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Discussion', 'Tartışma', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Disease', 'Hastalık', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Distance', 'Mesafe', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Divorced', 'Boşanmış', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Document', 'Belge', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Double', 'Çift', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Download', 'İndirmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Drama', 'Drama', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Drawing', 'Çizim', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dream', 'Rüya', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Driving', 'Sürüş', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Drop', 'Düşürmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Drug', 'İlaç', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dry', 'Kuru', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Earn', 'Para kazanmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Earth', 'Toprak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Easily', 'Kolayca', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Education', 'Eğitim', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Effect', 'Etki', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Either', 'Herhangi biri', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Electric', 'Elektrikli', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Electrical', 'Elektrik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Electricity', 'Elektrik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Electronic', 'Elektronik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Employ', 'Kullanmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Employee', 'Çalışan', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Employer', 'İş veren', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Empty', 'Boş', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ending', 'Son', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Energy', 'Enerji', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Engine', 'Motor', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Engineer', 'Mühendis', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Enormous', 'Kocaman', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Enter', 'Girmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Environment', 'Çevre', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Equipment', 'Teçhizat', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Error', 'Hata', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Especially', 'Özellikle', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Essay', 'Makale', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Everyday', 'Her gün', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Everywhere', 'Her yer', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Evidence', 'Kanıt', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Exact', 'Bire bir aynı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Exactly', 'Kesinlikle', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Excellent', 'Harika', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Except', 'Hariç', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Exist', 'Var olmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Expect', 'Beklemek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Experience', 'Deneyim', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Experiment', 'Deney', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Expert', 'Uzman', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Explanation', 'Açıklama', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Express', 'İfade etmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Expression', 'İfade', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Extreme', 'Aşırı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Extremely', 'Aşırı boyutta', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Factor', 'Faktör', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Factory', 'Fabrika', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fail', 'Hata', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fair', 'Adil', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fan', 'Fan', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Farming', 'Çiftçilik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fashion', 'Moda', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fear', 'Korku', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Feature', 'Özellik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Feed', 'Beslemek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Female', 'Dişi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fiction', 'Kurgu', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Field', 'Alan', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fight', 'Kavga', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Figure', 'Figür', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Finally', 'Sonunda', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Finger', 'Parmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Firstly', 'İlk önce', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fishing', 'Balık tutma', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fit', 'Yerleştirmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fix', 'Düzeltmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Flu', 'Nezle', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Flying', 'Uçan', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Focus', 'Odak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Following', 'Takip etme', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Foreign', 'Yabancı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Forest', 'Orman', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fork', 'Çatal', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Formal', 'Resmi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fortunately', 'Neyse ki', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Forward', 'İleri', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fresh', 'Taze', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fridge', 'Buzdolabı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Frog', 'Kurbağa', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Furniture', 'Mobilya', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Further', 'Daha öte', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Gallery', 'Galeri', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Gap', 'Açıklık', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Gas', 'Gaz', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Gate', 'Geçit', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('General', 'Genel', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Gift', 'Hediye', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Goal', 'Amaç', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('God', 'Tanrı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Gold', 'Altın', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Golf', 'Golf', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Government', 'Devlet', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Grass', 'Çimen', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Greet', 'Selamlaşmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ground', 'Zemin', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Guest', 'Misafir', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Guide', 'Rehber', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Gun', 'Silah', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Guy', 'Adam', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Habit', 'Alışkanlık', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hall', 'Salon', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Happily', 'Mutlu bir şekilde', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Headache', 'Baş ağrısı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Heart', 'Kalp', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Heat', 'Sıcaklık', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Heavy', 'Ağır', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Height', 'Yükseklik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Helpful', 'Yardımsever', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hero', 'Kahraman', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hers', 'Onun', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Herself', 'Kendini', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hide', 'Saklamak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hill', 'Tepe', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Himself', 'Kendisi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hit', 'Vurmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hockey', 'Hokey', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hold', 'Tutmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hole', 'Delik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Huge', 'Büyük', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Human', 'İnsan', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hurt', 'Acıtmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ideal', 'İdeal', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Identify', 'Tanımlamak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ill', 'Hasta', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Illness', 'Hastalık', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Image', 'İmaj', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Immediately', 'Hemen', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Impossible', 'İmkansız', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Included', 'Dahil', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Including', 'İçermek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Increase', 'Arttırmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Incredible', 'İnanılmaz', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Independent', 'Bağımsız', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Individual', 'Bireysel', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Industry', 'Sanayi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Informal', 'Gayri resmi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Injury', 'İncinme', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Insect', 'Böcek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Inside', 'İçeri', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Instead', 'Yerine', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Instruction', 'Talimat', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Instructor', 'Eğitmen', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Instrument', 'Enstrüman', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Intelligent', 'Zeki', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('International', 'Uluslararası', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Introduction', 'Giriiş', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Invent', 'İcat etmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Invention', 'Buluş', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Invitation', 'Davet', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Invite', 'Davet etmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Involve', 'İçermek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Item', 'Öğe', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Itself', 'Kendisi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Jam', 'Reçel', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Jazz', 'Caz', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Jewellery', 'Mücevher', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Joke', 'Şaka', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Journalist', 'Gazeteci', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Jump', 'Zıplamak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Kid', 'Çocuk', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Kill', 'Öldürmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('King', 'Kral', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Knee', 'Diz', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Knife', 'Bıçak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Knock', 'Vuruş', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Knowledge', 'Bilgi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lab', 'Laboratuvar', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lady', 'Bayan', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lake', 'Göl', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lamp', 'Lamba', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Laptop', 'Dizüstü bilgisayar', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Last', 'Son', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Laughter', 'Kahkaha', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Law', 'Kanun', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lawyer', 'Avukat', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lazy', 'Tembel', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lead', 'Yol göstermek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Leader', 'Lider', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Learning', 'Öğrenme', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Least', 'En az', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lecture', 'Konferans', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lemon', 'Limon', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lend', 'Ödünç vermek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Less', 'Az', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Level', 'Seviyesi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lifestyle', 'Yaşam tarzı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lift', 'Kaldırmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Light (a lamp)', 'Işık (bir lamba)', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Light (not heavy)', 'Işık (ağır değil)', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Likely', 'Büyük ihtimalle', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Link', 'Bağlantı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Listener', 'Dinleyici', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lock', 'Kilit', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lorry', 'Kamyon', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lost', 'Kayıp', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Loud', 'Yüksek sesle', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Loudly', 'Yüksek sesle', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lovely', 'Sevimli', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Low', 'Düşük', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Luck', 'Şans', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lucky', 'Şanslı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mail', 'Posta', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Major', 'Ana', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Male', 'Erkek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Manage', 'Üstesinden gelmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Manager', 'Müdür', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Manner', 'Biçim', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mark', 'İşaret', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Marry', 'Evlenmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Material', 'Malzeme', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mathematics', 'Matematik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Maths', 'Matematik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Matter', 'Konu', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('May modal', 'Mayıs modal', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Media', 'Medya', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Medical', 'Tıbbi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Medicine', 'İlaç', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Memory', 'Hafıza', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mention', 'Değinmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Metal', 'Metal', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Method', 'Yöntem', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Middle', 'Orta', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Might', 'Belki', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mind', 'Akıl', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mine (belongs to me)', 'Benim (bana ait)', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mirror', 'Ayna', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Missing', 'Eksik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mobile', 'Hareketli', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Monkey', 'Maymun', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Moon', 'Ay', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mostly', 'Çoğunlukla', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Motorcycle', 'Motosiklet', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Movement', 'Hareket', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Musical', 'Müzikal', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Musician', 'Müzisyen', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Myself', 'Kendim', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Narrow', 'Dar', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('National', 'Ulusal', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Nature', 'Doğa', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Nearly', 'Neredeyse', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Necessary', 'Gerekli', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Neck', 'Boyun', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Neither', 'Hiç biri', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Nervous', 'Gergin', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Network', 'Ağ', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Noise', 'Gürültü', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Noisy', 'Gürültülü', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('None', 'Hiçbiri', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Normal', 'Normal', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Normally', 'Normalde', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Notice', 'Fark etme', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Novel', 'Roman', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Nowhere', 'Hiçbir yerde', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Nut', 'Ceviz', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ocean', 'Okyanus', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Offer', 'Teklif', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Officer', 'Memur', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Oil', 'Yağ', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Onto', 'Üzerine', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Opportunity', 'Fırsat', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Option', 'Seçenek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ordinary', 'Sıradan', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Organisation', 'Organizasyon', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Organise', 'Organize etmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Original', 'Orijinal', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ourselves', 'Kendimizi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Oven', 'Fırın', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Owner', 'Mal sahibi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pack', 'Ambalaj', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pain', 'Ağrı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Painter', 'Ressam', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Palace', 'Saray', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pants', 'Pantolon', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Parking', 'Otopark', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Particular', 'Özel', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pass', 'Geçmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Passenger', 'Yolcu', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Patient', 'Hasta', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pattern', 'Model', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Peace', 'Barış', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Penny', 'Kuruş', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Per', 'Başına', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Per cent', 'Yüzde', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Perform', 'Rol yapmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Perhaps', 'Belki', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Permission', 'İzin', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Personality', 'Kişilik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pet', 'Evcil hayvan', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Petrol', 'Benzin', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Physical', 'Fiziksel', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Physics', 'Fizik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pick', 'Seçmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pilot', 'Pilot', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Planet', 'Gezegen', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Plastic', 'Plastik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Plate', 'Plaka', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Platform', 'Platform', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pleased', 'Memnun', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pocket', 'Cep', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Polite', 'Kibar', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pollution', 'Kirlilik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pop', 'Pop', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Population', 'Nüfus', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Position', 'Konum', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Possession', 'Mülk', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Possibility', 'Olasılık', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Poster', 'Poster', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Power', 'Güç', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Predict', 'Tahmin etmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('President', 'Başkan', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Prevent', 'Önlemek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Print', 'Basılı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Printer', 'Yazıcı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Prison', 'Hapishane', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Prize', 'Ödül', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Process', 'İşlem', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Produce', 'Üretmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Professional', 'Profesyonel', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Professor', 'Profesör', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Profile', 'Profil', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Program', 'Program', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Progress', 'İlerlemek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Promise', 'Söz', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pronounce', 'Telaffuz etmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Protect', 'Korumak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Provide', 'Sağlamak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pub', 'Pub', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Public', 'Halk', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Publish', 'Yayınlamak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pull', 'Çekmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Purpose', 'Amaç', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Push', 'İtmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Quality', 'Kalite', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Quantity', 'Miktar', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Queen', 'Kraliçe', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Quietly', 'Sessizce', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Race (run)', 'Yarış (koş)', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Railway', 'Demiryolu', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Raise', 'Artırmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rate', 'Oran', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rather', 'Yerine', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Reach', 'Ulaşmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('React', 'Tepki vermek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Realize', 'Fark etmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Receive', 'Almak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Recent', 'Son', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Recently', 'Son zamanlarda', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Reception', 'Resepsiyon', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Recipe', 'Yemek tarifi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Recognise', 'Tanımak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Recommend', 'Tavsiye etmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Record', 'Kayıt', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Recording', 'Kayıt', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Recycle', 'Geri dönüştürmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Reduce', 'Azaltmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Refer', 'Bahsetmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Refuse', 'Reddetmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Region', 'Bölge', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Regular', 'Düzenli', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Relationship', 'İlişki', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Remove', 'Kaldırmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Repair', 'Tamirat', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Replace', 'Yer değiştirmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Reply', 'Cevap vermek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Reporter', 'Muhabir', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Request', 'Rica etmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Research', 'Araştırma', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Researcher', 'Araştırmacı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Respond', 'Yanıtlamak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Response', 'Cevap', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rest', 'Dinlenmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Review', 'Gözden geçirmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ring', 'Yüzük', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rise', 'Yükselmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rock', 'Kaynak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Role', 'Rol', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Roof', 'Çatı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Round', 'Yuvarlak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Route', 'Rota', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rubbish', 'Zırva', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rude', 'Kaba', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Runner', 'Koşucu', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Running', 'Koşma', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sadly', 'Ne yazık ki', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Safe', 'Güvenli', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sail', 'Denize açılmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sailing', 'Yelken', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Salary', 'Maaş', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sale', 'Satış', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sauce', 'Sos', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Save', 'Kaydetmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Scared', 'Korkmuş', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Scary', 'Korkutucu', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Scene', 'Sahne', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Schedule', 'Takvim', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Score', 'Gol', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Screen', 'Ekran', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Search', 'Aramak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Season', 'Mevsim', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Seat', 'Koltuk', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Second (next)', 'İkincisi (sonraki)', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Secondly', 'İkincisi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Secret', 'Gizli', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Secretary', 'Sekreter', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Seem', 'Gözükmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sense', 'Algı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Separate', 'Ayırmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Series', 'Seri', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Serious', 'Cidden', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Serve', 'Sert', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Service', 'Hizmet vermek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Several', 'Birçok', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shake', 'Sallamak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shall', '-a', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shape', 'Şekil', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sheet', 'Çarşaf', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ship', 'Gemi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shoulder', 'Omuz', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shout', 'Bağırmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shut', 'Kapanmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Side', 'Taraf', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sign', 'İmza', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Silver', 'Gümüş', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Simple', 'Basit', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Since', 'O zamandan beri', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Singing', 'Şarkı söyleme', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Single', 'Bekar', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sir', 'Sayın', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Site', 'Alan', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Size', 'Boyut', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ski', 'Kayak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Skiing', 'Kayak yapma', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Skin', 'Deri', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sky', 'Gökyüzü', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Slowly', 'Yavaş yavaş', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Smartphone', 'Akıllı telefon', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Smell', 'Koku', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Smile', 'Gülümsemek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Smoke', 'Duman', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Smoking', 'Sigara içme', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Soap', 'Sabun', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Soccer', 'Futbol', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Social', 'Sosyal', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Society', 'Toplum', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sock', 'Çorap', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Soft', 'Yumuşak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Soldier', 'Asker', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Solution', 'Çözüm', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Solve', 'Çözmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Somewhere', 'Bir yerde', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sort', 'Düzenlemek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Source', 'Kaynak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Speaker', 'Konuşmacı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Specific', 'Özel', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Speech', 'Konuşma', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Speed', 'Hız', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Spider', 'Örümcek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Spoon', 'Kaşık', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Square', 'Kare', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stage', 'Sahne', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stair', 'Merdiven', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stamp', 'Damga vurmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('State', 'Durum', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Steal', 'Hırsızlık yapmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Step', 'Adım', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stomach', 'Karın', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stone', 'Taş', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Store', 'Mağaza', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Storm', 'Fırtına', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Straight', 'Dümdüz', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Strange', 'Garip', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Strategy', 'Strateji', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stress', 'Stres', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Structure', 'Yapı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stupid', 'Aptal', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Succeed', 'Başarmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Successful', 'Başarılı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Such', 'Çok', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Suddenly', 'Birden', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Suggest', 'Tavsiye etmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Suggestion', 'Telkin', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Suit', 'Uygun olmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Support', 'Destek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Suppose', 'Sanmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Surprise', 'Sürpriz', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Surprised', 'Şaşırmış', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Surprising', 'Şaşırtıcı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Survey', 'Anket', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sweet', 'Tatlı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Symbol', 'Sembol', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('System', 'Sistem', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tablet', 'Tablet', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Target', 'Hedef', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Task', 'Görev', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Taste', 'Tatmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Teaching', 'Öğretim', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Technology', 'Teknoloji', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Teenage', 'Genç', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Temperature', 'Sıcaklık', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Term', 'Terim', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Themselves', 'Kendileri', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Thick', 'Kalın', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Thief', 'Hırsız', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Thin', 'İnce', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Thinking', 'Düşünme', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Thought', 'Düşünce', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Throw', 'Fırlatmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tidy', 'Düzenli', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tie', 'Bağlamak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tip', 'Uç', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tool', 'Alet', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Top', 'Tepe', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Touch', 'Dokunmak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tour', 'Tur', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tourism', 'Turizm', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Towards', 'Karşı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Towel', 'Havlu', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tower', 'Kule', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Toy', 'Oyuncak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Track', 'İzlemek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tradition', 'Gelenek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Traditional', 'Geleneksel', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Trainer', 'Antrenör', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Training', 'Eğitim', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Transport', 'Taşıma', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Traveller', 'Gezgin', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Trouble', 'Bela', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Truck', 'Kamyon', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Twin', 'İkiz', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Typical', 'Tipik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Underground', 'Yeraltı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Understanding', 'Anlayışlı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Unfortunately', 'Ne yazık ki', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Unhappy', 'Mutsuz', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Uniform', 'Üniforma', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Unit', 'Birim', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('United', 'Birleşik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Unusual', 'Olağan dışı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Used to', 'Eskiden', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('User', 'Kullanıcı', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Usual', 'Olağan', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Valley', 'Vadi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Van', 'Minibüs', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Variety', 'Çeşitlilik', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Vehicle', 'Araç', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('View', 'Görüş', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Virus', 'Virüs', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Voice', 'Ses', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('War', 'Savaş', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Washing', 'Yıkama', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wave', 'Dalga', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Weak', 'Zayıf', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Web', 'Ağ', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wedding', 'Düğün', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Weight', 'Ağırlık', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wet', 'Islak', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wheel', 'Teker', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('While', 'Sırasında', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Whole', 'Tüm', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Whose', 'Kimin', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wide', 'Geniş', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wild', 'Vahşi', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wind', 'Rüzgâr', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Winner', 'Kazanan', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wish', 'Dilek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wood', 'Odun', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wooden', 'Ahşap', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Working', 'Çalışma', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Worried', 'Endişeli', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Worry', 'Endişelenmek', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Worse', 'Daha kötüsü', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Worst', 'En kötüsü', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wow', 'Vay', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Yet', 'Henüz', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Yours', 'Seninki', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Zero', 'Sıfır', 'A2', NULL, 0, 0);
INSERT INTO words
VALUES ('Absolutely', 'Kesinlikle', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Academic', 'Akademik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Access', 'Erişim', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Accommodation', 'Konaklama', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Account', 'Hesap', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Achievement', 'Başarı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ad', 'Reklam', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Addition', 'Ek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Admire', 'Beğenmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Admit', 'İtiraf etmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Advanced', 'Gelişmiş', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Advise', 'Öğüt vermek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Afford', 'Parası yetmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Aged', 'Yaşlı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Agent', 'Ajan', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Agreement', 'Anlaşma', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ahead', 'İlerde', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Aim', 'Amaç', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Alarm', 'Alarm vermek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Album', 'Albüm', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Alcohol', 'Alkol', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Alcoholic', 'Alkollü', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Amazed', 'Şaşkın', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ambition', 'Tutku', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ambitious', 'Hırslı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Analyse', 'Analiz etmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Analysis', 'Analiz', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Announce', 'Duyurmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Announcement', 'Duyuru', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Annoy', 'Kızdırmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Annoyed', 'Sinirli', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Annoying', 'Sinir bozucu', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Apart', 'Ayrı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Apologize', 'Özür dilemek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Application', 'Başvuru', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Appointment', 'Randevu', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Appreciate', 'Takdirle karşılamak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Approximately', 'Yaklaşık olarak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Arrest', 'Tutuklamak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Arrival', 'Varış', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Assignment', 'Atama', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Assist', 'Yardımcı olmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Atmosphere', 'Atmosfer', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Attach', 'Eklemek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Attitude', 'Davranış', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Attract', 'Cezbetmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Attraction', 'Cazibe', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Authority', 'Otorite', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Aware', 'Farkında olmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Backwards', 'Geriye doğru', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bake', 'Pişmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Balance', 'Denge', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ban', 'Yasaklamak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bank (river)', 'Banka (nehir)', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Base', 'Temel', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Basic', 'Temel', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Basis', 'Temel', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Battery', 'Pil', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Battle', 'Savaş', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Beauty', 'Güzellik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bee', 'Arı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Belief', 'İnanç', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bell', 'Zil', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bend', 'Bükülmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bite', 'Isırmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Block', 'Engellemek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bomb', 'Bomba', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Border', 'Sınır', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bother', 'Rahatsız etmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Branch', 'Şube', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Brand', 'Marka', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Brave', 'Cesur', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Breath', 'Nefes', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Breathe', 'Nefes almak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Breathing', 'Nefes alma', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bride', 'Gelin', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bubble', 'Kabarcık', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bury', 'Gömmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Calm', 'Sakinlik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Campaign', 'Kampanya', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Campus', 'Kampüs', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Candidate', 'Aday', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cap', 'Kapak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Captain', 'Kaptan', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Careless', 'Dikkatsiz', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Category', 'Kategori', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ceiling', 'Tavan', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Celebration', 'Kutlama', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Central', 'Merkezi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ceremony', 'Tören', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Chain', 'Zincir', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Challenge', 'Meydan okumak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Champion', 'Şampiyon', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Channel', 'Kanal', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Chapter', 'Bölüm', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Charge', 'Şarj', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cheat', 'Aldatmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cheerful', 'Neşeli', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Chemical', 'Kimyasal', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Chest', 'Göğüs', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Childhood', 'Çocukluk', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Claim', 'İddia', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Clause', 'Maddi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Click', 'Tıklamak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Client', 'Müşteri', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cloth', 'Bez', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Clue', 'İpucu', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Coal', 'Kömür', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Coin', 'Para', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Collection', 'Toplama', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Coloured', 'Renkli', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Combine', 'Birleştirmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Commercial', 'Reklam', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Commit', 'İşlemek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Communication', 'İletişim', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Comparison', 'Karşılaştırmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Competitor', 'Yarışmacı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Competitive', 'Rekabetçi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Complaint', 'Şikayet', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Complex', 'Karmaşık', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Concentrate', 'Konsantre olmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Conclude', 'Sonuçlandırmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Conclusion', 'Çözüm', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Confident', 'Kendinden emin', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Confirm', 'Onaylamak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Confuse', 'Kafası karışmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Confused', 'Kafası karışmış', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Connection', 'Bağlantı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Consequence', 'Sonuçlar', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Consist', 'Oluşmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Consume', 'Tüketmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Consumer', 'Tüketici', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Contact', 'Temas etmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Container', 'Konteyner', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Content', 'Memnun', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Continuous', 'Sürekli', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Contrast', 'Zıtlık', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Convenient', 'Uygun', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Convince', 'İkna etmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Costume', 'Kostüm', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cottage', 'Kır evi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cotton', 'Pamuk', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Countryside', 'Kırsal kesim', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Court', 'Mahkeme', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Covered', 'Kapalı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cruel', 'Acımasız', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cultural', 'Kültürel', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Currency', 'Para birimi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Current', 'Akım', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Currently', 'Şu anda', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Curtain', 'Perde', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Custom', 'Gelenek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Damage', 'Zarar', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Decade', 'On yıl', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Decorate', 'Süslemek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Define', 'Tanımlamak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Definite', 'Kesin', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Definition', 'Tanım', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Deliver', 'Teslim etmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Departure', 'Kalkış', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Despite', 'Aksine', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Destination', 'Varış noktası', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Determine', 'Belirlemek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Determined', 'Azimli', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Development', 'Gelişim', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Diagram', 'Diyagram', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Diamond', 'Elmas', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Difficulty', 'Zorluk', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Directly', 'Doğrudan', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dirt', 'Kir', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Disadvantage', 'Dezavantaj', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Disappointed', 'Hayal kırıklığına uğramış', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Disappointing', 'Hayal kırıklığı yaratan', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Discount', 'İndirim', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dislike', 'Sevmemek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Divide', 'Bölmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Documentary', 'Belgesel', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Donate', 'Bağış yapmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Doubt', 'Şüphe', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dressed', 'Giyinmiş', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Drum', 'Davul', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Drunk', 'Sarhoş', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Due', 'Vadesi dolmuş', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dust', 'Toz', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Duty', 'Görev', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Earthquake', 'Deprem', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Eastern', 'Doğu', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Economic', 'Ekonomik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Economy', 'Ekonomi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Edge', 'Kenar', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Editor', 'Editör', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Educate', 'Eğitmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Educated', 'Eğitilmiş', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Educational', 'Eğitici', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Effective', 'Etkili', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Effectively', 'Etkili bir şekilde', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Effort', 'Çaba', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Election', 'Seçim', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Element', 'Eleman', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Embarrassed', 'Utanmış', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Embarrassing', 'Utandırıcı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Emergency', 'Acil durum', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Emotion', 'Duygu', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Employment', 'İstihdam', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Encourage', 'Cesaretlendirmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Enemy', 'Düşman', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Engaged', 'Nişanlı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Engineering', 'Mühendislik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Entertain', 'Eğlendirmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Entertainment', 'Eğlence', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Entrance', 'Giriş', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Entry', 'Giriş', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Environmental', 'Çevre', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Episode', 'Bölüm', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Equal', 'Eşit', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Equally', 'Eşit derecede', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Escape', 'Kaçmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Essential', 'Gerekli', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Eventually', 'Sonunda', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Examine', 'İncelemek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Exchange', 'Değişme', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Excitement', 'Heyecanlanmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Exhibition', 'Sergi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Expand', 'Genişletmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Expected', 'Beklenen', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Expedition', 'Sefer', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Experienced', 'Deneyimli', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Explode', 'Patlamak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Explore', 'Keşfetmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Explosion', 'Patlama', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Export', 'İhracat', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fairly', 'Adil bir şekilde', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Familiar', 'Aşina', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fancy', 'Süslü', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fascinating', 'Büyüleyici', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fashionable', 'Şık', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fasten', 'Sabitlemek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Favour', 'İyilik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fence', 'Çit', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fighting', 'Kavga', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('File', 'Dosya', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Financial', 'Mali', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fitness', 'Fitness', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fixed', 'Sabit', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Flag', 'Bayrak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Flood', 'Sel basmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Flour', 'Un', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Flow', 'Akış', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fold', 'Katlamak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Folk', 'Halk', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Force', 'Güç', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Forever', 'Sonsuza kadar', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Frame', 'Çerçeve', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Freeze', 'Dondurmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Frequently', 'Sıklıkla', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Friendship', 'Dostluk', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Frighten', 'Korkutmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Frightened', 'Korkmuş', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Frightening', 'Korkutucu', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Frozen', 'Dondurulmuş', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fry', 'Yağda kızartmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fuel', 'Yakıt', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Function', 'İşlev', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fur', 'Kürk', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Garage', 'Garaj', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Gather', 'Toplamak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Generally', 'Genel olarak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Generation', 'Nesil', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Generous', 'Cömert', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Gentle', 'Nazik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Gentleman', 'Beyefendi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ghost', 'Hayalet', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Giant', 'Devasa', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Glad', 'Memnun', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Global', 'Küresel', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Glove', 'Eldiven', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Goods', 'Mal', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Grade', 'Seviye', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Graduate', 'Mezun olmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Grain', 'Tahıl', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Grateful', 'Minnettar', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Growth', 'Büyüme', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Guard', 'Koruma', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Guilty', 'Suçlu', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hang', 'Asmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Happiness', 'Mutluluk', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hardly', 'Zorlu', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Headline', 'Başlık', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Heating', 'Isıtma', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Heavily', 'Ağır', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Helicopter', 'Helikopter', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Highlight', 'Vurgulamak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Highly', 'Son derece', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hire', 'Kiralamak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Historic', 'Tarihi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Historical', 'Tarihsel', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Honest', 'Dürüst', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Horrible', 'Berbat', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Horror', 'Korku', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Host', 'Ev sahibi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hunt', 'Avlamak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hurricane', 'Kasırga', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hurry', 'Acele etmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Identity', 'Kimlik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ignore', 'Görmezden gelmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Illegal', 'Yasadışı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Imaginary', 'Hayali', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Immediate', 'Hemen', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Immigrant', 'Göçmen', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Impact', 'Darbe', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Import', 'İçe aktarmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Importance', 'Önem', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Impression', 'İzlenim', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Impressive', 'Etkileyici', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Improvement', 'Gelişim', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Incredibly', 'İnanılmaz', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Indeed', 'Aslında', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Indicate', 'Belirtmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Indirect', 'Dolaylı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Indoor', 'Kapalı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Indoors', 'Kapalı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Influence', 'Etkilemek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ingredient', 'Bileşen', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Injure', 'Yaralamak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Injured', 'Yaralı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Innocent', 'Masum', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Intelligence', 'İstihbarat', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Intend', 'Niyet etmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Intention', 'Niyet', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Invest', 'Yatırım', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Investigate', 'Araştırmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Involved', 'Dahil olmuş', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Iron', 'Ütü', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Issue', 'Sorun', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('IT', 'BT', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Journal', 'Dergi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Judge', 'Yargıç', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Keen', 'Hevesli', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Keyboard', 'Klavye', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Kick', 'Tekme atmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Killing', 'Öldürme', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Kind (caring)', 'Kibar (sevecen)', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Kiss', 'Öpücük', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Label', 'Etiket', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Laboratory', 'Laboratuvar', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lack', 'Eksiklik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Latest', 'En sonuncu', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lay', 'Sermek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Layer', 'Katman', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Leading', 'Önde gelen', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Leaf', 'Yaprak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Leather', 'Deri', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Legal', 'Yasal', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Leisure', 'Boş vakit', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Length', 'Uzunluk', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lie (tell a lie)', 'Yalan (yalan söyle)', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Limit', 'Sınırlamak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lip', 'Dudak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Liquid', 'Sıvı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Literature', 'Edebiyat', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Living', 'Yaşamak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Locate', 'Bulmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Located', 'Yerleşmiş', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Location', 'Konum', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lonely', 'Yalnız', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Loss', 'Kayıp', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Luxury', 'Lüks', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mad', 'Kızgın', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Magic', 'Büyü', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mainly', 'Daha çok', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mall', 'Alışveriş merkezi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Management', 'Yönetmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Marketing', 'Pazarlama', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Marriage', 'Evlilik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Meanwhile', 'Bu sırada', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Measure', 'Ölçüm', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Medium', 'Orta', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mental', 'Akıllı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mess', 'Karışıklık', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mild', 'Hafif', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mine (hole)', 'Benim (delik)', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mix', 'Karışım', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mixture', 'Karışım', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mood', 'Mod', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mud', 'Çamur', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Murder', 'Cinayet', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Muscle', 'Kas', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mystery', 'Gizem', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nail', 'Çivi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Narrative', 'Anlatı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nation', 'Ulus', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Native', 'Yerli', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Naturally', 'Doğal olarak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Necessarily', 'Zorunlu olarak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Needle', 'İğne', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Neighbourhood', 'Komşu', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Net', 'Açık', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nor', 'Ne', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Northern', 'Kuzey', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nuclear', 'Nükleer', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Obvious', 'Bariz', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Obviously', 'Açıkça', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Occasion', 'Durum', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Occur', 'Meydana gelmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Odd', 'Garip', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Official', 'Resmi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Old-fashioned', 'Eski moda', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Operation', 'Ameliyat', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Organised', 'Organize edilmiş', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Organiser', 'Organizatör', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Originally', 'Başlangıçta', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ought', 'Zorla', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ours', 'Bizim', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Outdoor', 'Dış mekan', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Outdoors', 'Açık havada', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Package', 'Paketi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Painful', 'Acı verici', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pale', 'Solgun', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pan', 'Tava', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Participate', 'Katılmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Particularly', 'Özellikle', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Passion', 'Tutku', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Path', 'Yol', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Payment', 'Ödeme', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Peaceful', 'Barışçıl', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Percentage', 'Yüzdesi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Perfectly', 'Mükemmel bir şekilde', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Performance', 'Performans', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Personally', 'Kişisel olarak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Persuade', 'İkna etmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Photographer', 'Fotoğrafçı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Photography', 'Fotoğrafçılık', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pin', 'Pin', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pipe', 'Boru', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Planning', 'Planlama', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pleasant', 'Güzel', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pleasure', 'Zevk', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Plenty', 'Bolca', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Plot', 'Komplo', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Plus', 'Artı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Poem', 'Şiir', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Poet', 'Şair', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Poetry', 'Şiir', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Poison', 'Zehir', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Poisonous', 'Zehirli', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Policy', 'Politika', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Political', 'Siyasi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Politician', 'Siyasetçi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Politics', 'Siyaset', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Port', 'Liman', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Portrait', 'Portre', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Possibly', 'Muhtemelen', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pot', 'Tencere', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pour', 'Dökmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Poverty', 'Yoksulluk', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Powder', 'Toz', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Powerful', 'Güçlü', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Practical', 'Pratik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pray', 'Dua etmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prayer', 'Namaz', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prediction', 'Tahmin', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prepared', 'Tedarikli', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Presentation', 'Sunum', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Press', 'Basmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pressure', 'Basınç', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pretend', 'Numara yapmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Previous', 'Öncesi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Previously', 'Önceden', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Priest', 'Rahip', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Primary', 'Öncelik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prince', 'Prens', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Princess', 'Prenses', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Printing', 'Baskı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prisoner', 'Mahkum', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Private', 'Özel', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Producer', 'Yapımcı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Production', 'Üretme', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Profession', 'Meslek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Profit', 'Kâr', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Promote', 'Terfi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Proper', 'Düzgün', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Properly', 'Düzgün bir şekilde', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Property', 'Mülk', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Protest', 'Protesto', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Proud', 'Gurur duymak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prove', 'Kanıtlamak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Punish', 'Cezalandırmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Punishment', 'Ceza', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Qualification', 'Vasıf', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Qualified', 'Nitelikli', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Qualify', 'Hak kazanmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Queue', 'Sıra', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Quit', 'Çıkış yapmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Quotation', 'Alıntı yapmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Quote', 'Alıntı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Race (people)', 'Yarış (insanlar)', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Racing', 'Yarışma', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Range', 'Menzil', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rare', 'Nadir', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rarely', 'Nadiren', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reaction', 'Tepki', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reality', 'Gerçeklik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Receipt', 'Fiş', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Recommendation', 'Tavsiye', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reference', 'Referans', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reflect', 'Yansıtmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Regularly', 'Düzenli olarak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reject', 'Reddetmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Relate', 'İlgili olmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Related', 'İlgili', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Relation', 'İlişki', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Relative', 'Akraba', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Relaxed', 'Rahat', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Relaxing', 'Rahatlatıcı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Release', 'Serbest bırakmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reliable', 'Güvenilir', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Religion', 'Din', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Religious', 'Din', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Remain', 'Geriye kalmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Remind', 'Hatırlatmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Remote', 'Uzak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rent', 'Kira', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Repeated', 'Tekrarlanan', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Represent', 'Temsil etmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Require', 'Gerekmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reservation', 'Rezervasyon', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Resource', 'Kaynak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Respect', 'Saygı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Responsibility', 'Sorumluluk', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Responsible', 'Sorumlu', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Retire', 'Emekli olmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Retired', 'Emekli', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Revise', 'Tekrar düzeltme yapmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Risk', 'Risk', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Robot', 'Robot', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Roll', 'Rulo', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Romantic', 'Romantik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rope', 'Halat', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rough', 'Kaba ', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Row', 'Sıra', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Royal', 'Asil', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rugby', 'Rugby', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Safety', 'Emniyet', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sailor', 'Denizci', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sample', 'Örnek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sand', 'Kum', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Scan', 'Taramak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Scientific', 'İlmi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Script', 'Senaryo', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sculpture', 'Heykel', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Secondary', 'İkincil', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Security', 'Güvenlik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Seed', 'Tohum', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sensible', 'Mantıklı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Seriously', 'Cidden', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Servant', 'Hizmetkar', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Set (put)', 'Set (koy)', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Set (group)', 'Set (grup)', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Setting', 'Ayar', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sex', 'Seks', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sexual', 'Cinsel', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sharp', 'Keskin', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shelf', 'Raf', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shell', 'Kabuk', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shift', 'Vardiya', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shine', 'Parlamak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shiny', 'Parlak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shoot', 'Film çekmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shy', 'Utangaç', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sight', 'Görünüş', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Signal', 'Sinyal', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Silent', 'Sessiz', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Silly', 'Şapşal', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Similarity', 'Benzerlik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Similarly', 'Benzer şekilde', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Simply', 'Basitçe', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sink', 'Atmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Slice', 'Dilim', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Slightly', 'Biraz', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Smart', 'Akıllı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Smooth', 'Düz', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Software', 'Yazılım', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Soil', 'Toprak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Solid', 'Sağlam', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Southern', 'Güney', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Specifically', 'Özellikle', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spending', 'Harcama', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spicy', 'Baharatlı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spirit', 'Ruh', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spoken', 'Sözlü', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spot', 'Leke', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spread', 'Yaymak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stadium', 'Stadyum', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Staff', 'Kadro', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Standard', 'Standart', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Statistic', 'İstatistik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Statue', 'Heykel', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stick (attach)', 'Sopa (tak)', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stick (piece of wood)', 'Sopa (ahşap parçası)', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stranger', 'Yabancı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Strength', 'Kuvvet', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('String', 'Sicim', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Strongly', 'Kuvvetle', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Studio', 'Stüdyo', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stuff', 'Şey', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Substance', 'Madde', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Successfully', 'Başarıyla', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sudden', 'Birden', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Suffer', 'Acı çekmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Suitable', 'Uygun', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Summarise', 'Özetlemek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Summary', 'Özet', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Supply', 'Tedarik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Supporter', 'Destekçi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Surely', 'Elbette', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Surface', 'Yüzey', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Survive', 'Hayatta kalmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Switch', 'Anahtar', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Symptom', 'Semptom', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tail', 'Kuyruk', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Talent', 'Yetenek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Talented', 'Yetenekli', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tape', 'Kaset', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tax', 'Vergi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Technical', 'Teknik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Technique', 'Teknik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tend', 'Eğilimli olma', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tent', 'Çadır', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Theirs', 'Onların', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Theme', 'Tema', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Theory', 'Teori', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Therefore', 'Öyleyse', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Though', 'Yine de', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Throat', 'Boğaz', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Throughout', 'Boyunca', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tight', 'Sıkı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Till', 'Kadar', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tin', 'Kalay', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tiny', 'Minik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Toe', 'Ayak parmağı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tongue', 'Dil', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Total', 'Toplam', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Totally', 'Tamamen', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Trade', 'Ticaret', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Translate', 'Tercüme etmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Translation', 'Çeviri', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Treat', 'Davranmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Treatment', 'Tedavi', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Trend', 'Eğilim', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Trick', 'Kandırmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Truth', 'Gerçek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tube', 'Tüp', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Typically', 'Tipik olarak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tyre', 'Yorulmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ugly', 'Çirkin', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Unable', 'Aciz', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Uncomfortable', 'Rahatsız', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Underwear', 'İç çamaşırı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Unemployed', 'İşsiz', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Unemployment', 'İşsizlik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Unfair', 'Haksız', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Union', 'Birlik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Unless', 'Meğer ki', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Unlike', 'Farklı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Unlikely', 'İhtimal', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Unnecessary', 'Gereksiz', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Unpleasant', 'Tatsız', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Update', 'Güncelleme', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Upon', 'Üzerine', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Upset', 'Üzgün', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Used', 'Kullanılmış', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Valuable', 'Değerli', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Value', 'Değer', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Various', 'Çeşitli', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Version', 'Versiyon', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Victim', 'Kurban', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Viewer', 'İzleyici', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Violent', 'Şiddetli', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Volunteer', 'Gönüllü', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Vote', 'Oy', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Warn', 'Uyarmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Warning', 'Uyarı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Waste', 'Atık', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Weapon', 'Silah', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Weigh', 'Tartmak', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Western', 'Batılı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Whatever', 'Her neyse', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Whenever', 'Her ne zaman', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Whether', 'İkisinden biri', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Wing', 'Kanat', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Within', 'İçinde', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Wonder', 'Merak etmek', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Wool', 'Yün', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Worldwide', 'Dünya çapında', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Worth', 'Değer', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Written', 'Yazılı', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Yard', 'Bahçe', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Youth', 'Gençlik', 'B1', NULL, 0, 0);
INSERT INTO words
VALUES ('Abandon', 'Terk etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Absolute', 'Mutlak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Absorb', 'Emmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Abstract', 'Soyut', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Accent', 'Aksan', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Acceptable', 'Kabul edilebilir', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Accidentally', 'Kazara', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Accommodate', 'Uyum sağlamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Accompany', 'Eşlik etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Accomplish', 'Sonuçlandırmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Accountant', 'Muhasebeci', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Accuracy', 'Kesinlik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Accurate', 'Kesin', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Accurately', 'Doğruca', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Accuse', 'Suçlamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Acknowledge', 'Kabullenmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Acquire', 'Elde etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Acre', 'Dönüm', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Activate', 'Etkinleştirmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Actual', 'Gerçek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Adapt', 'Adapte olmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Addiction', 'Bağımlılık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Additional', 'Ek olarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Additionally', 'Ek olarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Adequate', 'Yeterli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Adequately', 'Yeterince', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Adjust', 'Ayarlamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Administration', 'Yönetim', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Adopt', 'Evlat edinmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Advance', 'İlerlemek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Affair', 'Mesele', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Affordable', 'Ekonomik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Afterwards', 'Daha sonrasında', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Agency', 'Ajans', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Agenda', 'Gündem', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Aggressive', 'Saldırgan', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Agriculture', 'Tarım', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Aid', 'Yardım', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('AIDS', 'AIDS', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Aircraft', 'Uçak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Alien', 'Yabancı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Alongside prep', 'Hazırlık ile birlikte', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Alter', 'Değiştirmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Altogether', 'Tamamen', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ambulance', 'Ambulans', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Amusing', 'Eğlenceli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Analyst', 'Analist', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ancestor', 'Ata', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Anger', 'Kızgınlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Angle', 'Açılış', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Animation', 'Animasyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Anniversary', 'Yıl dönümü', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Annual', 'Yıllık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Annually', 'Her yıl', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Anticipate', 'Beklemek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Anxiety', 'Endişe', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Anxious', 'Endişeli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Apology', 'Özür', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Apparent', 'Görünen', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Apparently', 'Görünüşe göre', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Appeal', 'Çekici', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Applicant', 'Başvuru sahibi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Approach', 'Yaklaşmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Appropriate', 'Uygun', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Appropriately', 'Uygun olarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Approval', 'Onay', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Approve', 'Onaylamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Arise', 'Kalkmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Armed', 'Silahlı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Arms', 'Kol', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Arrow', 'Ok', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Artificial', 'Yapay', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Artistic', 'Sanatsal', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Artwork', 'Sanat eseri', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ashamed', 'Utanmış', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Aspect', 'Bakış açısı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Assess', 'Değer biçmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Assessment', 'Değerlendirme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Asset', 'Varlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Assign', 'Atamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Assistance', 'Yardım', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Associate', 'İş arkadaşı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Associated', 'Birleşmiş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Association', 'Dernek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Assume', 'Farz etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Assumption', 'Varsayım', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Assure', 'Garanti etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Astonishing', 'Şaşırtıcı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Athletic', 'Atletik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Attachment', 'EK', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Attempt', 'Girişim', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Audio', 'Ses', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Awareness', 'Farkındalık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Awkward', 'Garip', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bacteria', 'Bakteriler', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Badge', 'Rozet', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Balanced', 'Dengeli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ballet', 'Bale', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Balloon', 'Balon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Barely', 'Neredeyse', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bargain', 'Pazarlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Barrier', 'Engel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Basement', 'Bodrum', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Basically', 'Temel olarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Basket', 'Sepet', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bat', 'Yarasa', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bear (deal with)', 'Ayı (anlaşma)', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Beg', 'Yalvarmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Being', 'Yapı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Beneficial', 'Yararlı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bent', 'Bükülmüş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Beside prep', 'Hazırlık''ın yanında', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Besides prep', 'Hazırlık dışında', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bet', 'Bahis', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Beyond', 'Öte', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bias', 'Ön yargı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bid', 'Teklif etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Biological', 'Biyolojik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bitter', 'Acı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Blame', 'Suçlamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Blanket', 'Battaniye', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Blind', 'Kör', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bold', 'Gözü pek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bombing', 'Bombalama', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bond', 'Bağlamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Boost', 'Artırmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bound', 'Sınır', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Breast', 'Göğüs', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Brick', 'Tuğla', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Brief', 'Kısa bilgi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Briefly', 'Kısaca', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Broad', 'Geniş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Broadcast', 'Yayın', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Broadcaster', 'Yayıncı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Broadly', 'Genel olarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Buck', 'Boks yapmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Budget', 'Bütçe', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bug', 'Böcek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bullet', 'Mermi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bunch', 'Demet', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bush', 'Çalı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('But (prep)', 'Ama (hazırlık)', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cabin', 'Kabin', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cable', 'Kablo', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Calculate', 'Hesaplamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Canal', 'Kanal', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cancel', 'İptal etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cancer', 'Kanser', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Candle', 'Mum', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Capable', 'Yetenekli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Capacity', 'Kapasite', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Capture', 'Esir almak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Carbon', 'Karbon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cast', 'Döküm', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Casual', 'Gündelik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cave', 'Mağara', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cell', 'Hücre', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Certainty', 'Kesinlik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Certificate', 'Sertifika', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Chairman', 'Başkan', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Challenging', 'Zorlu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Championship', 'Şampiyonluk', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Characteristic', 'Karakteristik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Charming', 'Alımlı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Chase', 'Takip etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cheek', 'Yanak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cheer', 'Tezahürat etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Chief', 'Başkanlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Chop', 'Kesmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Circuit', 'Devre', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Circumstance', 'Durum', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cite', 'Alıntı yapmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Citizen', 'Vatandaş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Civil', 'Sivil', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Civilization', 'Medeniyet', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Clarify', 'Açıklamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Classic', 'Klasik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Classify', 'Sınıflandırmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cliff', 'Uçurum', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Clinic', 'Klinik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Clip', 'Klips', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Closely', 'Yakından', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Coincidence', 'Tesadüf', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Collapse', 'Yıkılmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Collector', 'Kolektör', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Colony', 'Koloni', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Colorful', 'Renkli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Combination', 'Kombinasyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Comfort', 'Rahatlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Comic', 'Çizgi roman', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Command', 'Emretmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Commander', 'Komutan', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Commission', 'Komisyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Commitment', 'Bağlılık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Committee', 'Komite', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Commonly', 'Yaygın olarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Comparative', 'Karşılaştırmalı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Completion', 'Tamamlama', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Complicated', 'Karmaşık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Component', 'Bileşen', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Compose', 'Bestelemek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Composer', 'Besteci', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Compound', 'Birleştirmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Comprehensive', 'Kapsayıcı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Comprise', 'İçermek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Compulsory', 'Zorunlu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Concentration', 'Konsantrasyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Concept', 'Kavram', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Concern', 'Kaygı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Concerned', 'Endişeli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Concrete', 'Beton', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Conduct', 'Yönetmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Confess', 'İtiraf etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Confidence', 'Kendinden emin', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Conflict', 'Anlaşmazlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Confusing', 'Kafa karıştırıcı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Confusion', 'Bilinç bulanıklığı, konfüzyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Conscious', 'Bilinçli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Consequently', 'Sonuç olarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Conservation', 'Koruma', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Conservative', 'Tutucu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Considerable', 'Hatırı sayılır', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Considerably', 'Çok', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Consideration', 'Düşünce', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Consistent', 'Tutarlı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Consistently', 'Sürekli olarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Conspiracy', 'Komplo', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Constant', 'Devamlı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Constantly', 'Sürekli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Construct', 'Yapmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Construction', 'Yapı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Consult', 'Danışmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Consultant', 'Danışman', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Consumption', 'Tüketim', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Contemporary', 'Modern', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Contest', 'Yarışma', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Contract', 'Sözleşme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Contribute', 'Katkı yapmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Contribution', 'Katkı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Controversial', 'Tartışmalı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Controversy', 'Tartışma', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Convenience', 'Kolaylık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Convention', 'Kongre', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Conventional', 'Geleneksel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Convert', 'Dönüştürmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Convey', 'İletmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Convinced', 'İkna edilmiş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Convincing', 'İnandırıcı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cop', 'Polis', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cope', 'Başa çıkmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Core', 'Çekirdek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Corporate', 'Kurumsal', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Corporation', 'Şirket', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Corridor', 'Koridor', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Council', 'Konsey', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Counter (flat surface)', 'Sayaç (düz yüzey)', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('County', 'Kontluk', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Courage', 'Cesaret', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Coverage', 'Kapsama alanı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cowboy', 'Kovboy', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Crack', 'Çatırtı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Crash', 'Kaza', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Creation', 'Yaratma', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Creativity', 'Yaratıcılık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Creature', 'Yaratık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Crew', 'Mürettebat', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Crisis', 'Kriz', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Criterion', 'Kriter', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Critic', 'Eleştirmen', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Critical', 'Eleştirel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Critically', 'Eleştirel olarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Criticism', 'Eleştiri', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Criticize', 'Eleştirmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Crop', 'Mahsul', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Crucial', 'Hayati', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cruise', 'Dolaşmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cue', 'İşaret', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cure', 'Tedavi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Curious', 'Meraklı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Curriculum', 'Müfredat', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Curve', 'Eğri', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Curved', 'Kavisli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cute', 'Sevimli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dairy', 'Günlük', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dare', 'Cesaret', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Darkness', 'Karanlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Database', 'Veritabanı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Deadline', 'Son teslim tarihi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Deadly', 'Ölümcül', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dealer', 'Satıcı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Debate', 'Çekişme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Debt', 'Borç', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Decent', 'Düzgün', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Deck', 'Güverte', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Declare', 'İlan etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Decline', 'Reddetmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Decoration', 'Dekorasyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Decrease', 'Azaltmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Deeply', 'Derinlemesine', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Defeat', 'Yenmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Defence', 'Savunma', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Defend', 'Savunmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Defender', 'Savunmacı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Delay', 'Gecikme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Delete', 'Silmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Deliberate', 'Kasıtlı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Deliberately', 'Kasten', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Delight', 'Zevk', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Delighted', 'Memnun', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Delivery', 'Teslimat', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Demand', 'Talep etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Democracy', 'Demokrasi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Democratic', 'Demokratik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Demonstrate', 'Göstermek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Demonstration', 'Gösteri', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Deny', 'Reddetmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Depart', 'Ayrılmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dependent', 'Bağımlı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Deposit', 'Yatırmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Depressed', 'Depresif', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Depressing', 'İç karartıcı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Depression', 'Depresyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Depth', 'Derinlik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Derive', 'Türetmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Deserve', 'Hak etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Desire', 'Arzu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Desperate', 'Çaresiz', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Desperately', 'Umutsuzca', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Destruction', 'Yıkım', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Detailed', 'Ayrıntılı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Detect', 'Tespit etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Determination', 'Kararlılık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Devote', 'Adamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Differ', 'Farketmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dig', 'Kazmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dime', 'Kurnazlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Disability', 'Sakatlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Disabled', 'Engelli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Disagreement', 'Anlaşmazlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Disappoint', 'Hayal kırıklığına uğratmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Disappointment', 'Hayal kırıklığı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Disc', 'Disk', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Discipline', 'Disiplin', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Discourage', 'Vazgeçirmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dishonest', 'Sahtekâr', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dismiss', 'Azletmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Disorder', 'Düzensizlik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Display', 'Görüntülemek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Distant', 'Mesafe', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Distinct', 'Belirgin', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Distinguish', 'Ayırt etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Distract', 'Rahatsız etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Distribute', 'Dağıtmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Distribution', 'Dağıtım', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('District', 'Semt', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Disturb', 'Rahatsız etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dive', 'Dalmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Diverse', 'Türlü', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Diversity', 'Çeşitlilik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Division', 'Bölüm', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Divorce', 'Boşanmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Domestic', 'Yerel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dominant', 'Baskın', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dominate', 'Hakim olmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Donation', 'Bağış', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dot', 'Nokta', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Downwards', 'Aşağı doğru', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dozen', 'Düzine', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Draft', 'Taslak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Drag', 'Sürüklemek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dramatic', 'Dramatik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dramatically', 'Dramatik bir şekilde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Drought', 'Kuraklık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dull', 'Sıkıcı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dump', 'Çöplük', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Duration', 'Süre', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dynamic', 'Dinamik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Eager', 'İstekli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Economics', 'Ekonomi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Economist', 'İktisatçı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Edit', 'Düzenlemek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Edition', 'Sürüm', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Editorial', 'Başyazı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Efficient', 'Verimli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Efficiently', 'Verimli bir şekilde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Elbow', 'Dirsek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Elderly', 'Yaşlı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Elect', 'Seçmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Electronics', 'Elektronik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Elegant', 'Zarif', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Elementary', 'Temel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Eliminate', 'Elemek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Elsewhere', 'Başka yerlerde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Embrace', 'Kucaklamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Emerge', 'Meydana gelmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Emission', 'Emisyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Emotional', 'Duygusal', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Emotionally', 'Duygusal olarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Emphasis', 'Vurgu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Emphasize', 'Vurgu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Empire', 'İmparatorluk', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Enable', 'Olanak vermek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Encounter', 'Rastlamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Engage', 'Angaje etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Enhance', 'Genişletmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Enjoyable', 'Eğlenceli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Enquiry', 'Sorgu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ensure', 'Emin olmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Entertaining', 'Eğlenceli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Enthusiasm', 'Heves', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Enthusiastic', 'Hevesli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Entire', 'Bütün', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Entirely', 'Tamamen', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Entrepreneur', 'Girişimci', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Envelope', 'Mektup', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Equip', 'Donatmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Equivalent', 'Eş değer', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Era', 'Dönem', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Erupt', 'Patlak vermek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Essentially', 'Esasen', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Establish', 'Kurmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Estate', 'Emlak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Estimate', 'Tahmin etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ethic', 'Etik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ethical', 'Etik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ethnic', 'Etnik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Evaluate', 'Değerlendirmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Evaluation', 'Değerlendirme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Evident', 'Açık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Evil', 'Fenalık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Evolution', 'Evrim', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Evolve', 'Evrim geçirmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Examination', 'Muayene', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Exceed', 'Aşmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Exception', 'İstisna', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Excessive', 'Aşırı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Exclude', 'Hariç tutmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Excuse', 'Mazeret', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Executive', 'Yönetici', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Existence', 'Varoluş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Exotic', 'Egzotik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Expansion', 'Genleşme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Expectation', 'Beklenti', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Expense', 'Masraf', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Expertise', 'Uzmanlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Exploit', 'Faydalanmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Exploration', 'Keşif', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Expose', 'Ortaya çıkarmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Exposure', 'Maruziyet', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Extend', 'Uzatmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Extension', 'Eklenti', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Extensive', 'Yaygın', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Extensively', 'Kapsamlı bir şekilde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Extent', 'Kapsam', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('External', 'Harici', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Extract', 'Çıkarmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Extraordinary', 'Olağanüstü', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fabric', 'Kumaş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fabulous', 'Efsanevi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Facility', 'Tesis', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Faculty', 'Fakülte', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Failed', 'Arızalı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Failure', 'Arıza', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Faith', 'İnanç', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fake', 'Sahte', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fame', 'Şöhret', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fantasy', 'Fantezi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fare', 'Ücret', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fault', 'Arıza', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Feather', 'Tüy', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fee', 'Ücret', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Feedback', 'Geri bildirim', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fellow', 'Aynı tür', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Finance', 'Finans', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Finding', 'Bulma', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Firefighter', 'İtfaiyeci', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Firework', 'Havai fişek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Firm', 'Firma', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Firmly', 'Sıkıca', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Flame', 'Alev', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Flash', 'Flaş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Flavor', 'Lezzet', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Flexible', 'Esnek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Float', 'Batmadan yüzmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Folding', 'Katlanır', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fond', 'Düşkün', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fool', 'Aptal', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Forbid', 'Yasaklamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Forecast', 'Tahmin etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Forgive', 'Affetmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Format', 'Biçim', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Formation', 'Oluşum', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Former', 'Önceki', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Formerly', 'Eski', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fortunate', 'Şanslı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fortune', 'Talih', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Forum', 'Forum', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fossil', 'Fosil', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Found', 'Kurmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Foundation', 'Temel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Founder', 'Kurucu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fraction', 'Fraksiyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fragment', 'Fragman', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Framework', 'Çerçeve', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fraud', 'Sahtekar', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Freedom', 'Özgürlük', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Freely', 'Serbestçe', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Frequency', 'Sıklık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Frequent', 'Sık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fulfill', 'Yerine getirmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Full-time/adv', 'Tam zamanlı/adv', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fully', 'Tam', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fund', 'Para', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fundamental', 'Esas', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fundamentally', 'Temelde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Funding', 'Finansman', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Furious', 'Çok öfkeli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Furthermore', 'Üstelik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Gain', 'Kazanmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Gallon', 'Galon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Gaming', 'Oyun', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Gang', 'Çete', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Gay', 'Eşcinsel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Gender', 'Cinsiyet', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Gene', 'Gen', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Generate', 'Üretmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Genetic', 'Genetik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Genius', 'Dahi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Genre', 'Tür', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Genuine', 'Eşsiz', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Genuinely', 'Gerçekten', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Gesture', 'Jest yapmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Globalization', 'Küreselleşme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Globe', 'Kürek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Golden', 'Altın', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Goodness', 'İyilik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Gorgeous', 'Muhteşem', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Govern', 'Yönetme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Grab', 'Yakalamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Gradually', 'Gitgide', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Grand', 'Büyük', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Grant', 'Hibe etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Graphic', 'Grafik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Graphics', 'Grafikler', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Greatly', 'Büyük ölçüde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Greenhouse', 'Sera', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Guarantee', 'Garanti', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Guideline', 'Kılavuz', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Habitat', 'Doğal ortam', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Handle', 'Halletmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Harbor', 'Liman', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Harm', 'Zarar', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Harmful', 'Zararlı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Headquarters', 'Karargah', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Heal', 'İyileştirmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Healthcare', 'Sağlık hizmeti', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hearing', 'İşitme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Heaven', 'Cennet', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Heel', 'Topuk', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hell', 'Cehennem', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Helmet', 'Kask', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hence', 'Buradan', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Herb', 'Bitki', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hesitate', 'Tereddüt etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hidden', 'Gizlenmiş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hilarious', 'Gülünç', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hip', 'Belki', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Historian', 'Tarihçi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hollow', 'Oyuk', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Holy', 'Kutsal', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Homeless', 'Evsiz', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Honesty', 'Dürüstlük', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Honey', 'Bal', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Honour', 'Onur', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hook', 'Çengel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hopefully', 'Umutla', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Household', 'Ev', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Housing', 'Konut', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Humorous', 'Esprili', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Humour', 'Mizah', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hunger', 'Açlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hunting', 'Avcılık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hypothesis', 'Hipotez', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Icon', 'Simge', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('ID', 'İD', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Identical', 'Birebir aynı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Illusion', 'Yanılsama', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Illustrate', 'Tasvir etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Illustration', 'Gösterim', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Imagination', 'Hayal gücü', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Immigration', 'Göçmenlik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Immune', 'Bağışık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Impatient', 'Sabırsız', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Implement', 'Uygulamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Implication', 'İma', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Imply', 'İma etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Impose', 'Empoze etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Impress', 'Etkilemek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Impressed', 'Etkilenmiş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Incentive', 'Teşvik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Inch', 'İnç', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Incident', 'Olay', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Income', 'Gelir', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Incorporate', 'Firma kurmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Incorrect', 'Yanlış', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Increasingly', 'Giderek daha fazla', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Independence', 'Bağımsızlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Index', 'İndeks', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Indication', 'Gösterge', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Industrial', 'Sanayi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Inevitable', 'Kaçınılmaz', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Inevitably', 'Kaçınılmaz olarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Infection', 'Enfeksiyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Infer', 'Çıkarım', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Inflation', 'Enflasyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Info', 'Bilgi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Inform', 'Bilgi vermek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Infrastructure', 'Altyapı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Inhabitant', 'Sakin', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Inherit', 'Miras almak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Initial', 'İlk', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Initially', 'İlk olarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Initiative', 'Girişim', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ink', 'Mürekkep', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Inner', 'İç', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Innovation', 'Yenilik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Innovative', 'Yenilikçi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Input', 'Giriş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Insert', 'Sokmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Insight', 'İç yüzü', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Insist', 'Israr etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Inspector', 'Müfettiş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Inspire', 'İlham vermek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Install', 'Düzenlemek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Installation', 'Kurulum', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Instance', 'Misal', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Instant', 'Ani', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Instantly', 'Aniden', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Institute', 'Enstitü', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Institution', 'Kurum', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Insurance', 'Sigorta', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Integrate', 'Entegre etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Intellectual', 'Entelektüel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Intended', 'Amaçlanan', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Intense', 'Yoğun', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Interact', 'Etkileşime girmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Interaction', 'Etkileşim', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Internal', 'Dahili', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Interpret', 'Yorumlamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Interpretation', 'Tercüme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Interrupt', 'Yarıda kesmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Interval', 'Aralık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Invade', 'İstila etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Invasion', 'İstila', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Investigation', 'Soruşturma', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Investment', 'Yatırım', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Investor', 'Yatırımcı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Isolate', 'İzole etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Isolated', 'İzole edilmiş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Jail', 'Hapishane', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Jet', 'Jet', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Joint', 'Eklem yeri', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Journalism', 'Gazetecilik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Joy', 'Neşe', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Judgement', 'Yargılama', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Junior', 'Genç', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Jury', 'Jüri', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Justice', 'Adalet', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Justify', 'Savunmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Kindergarten', 'Anaokulu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Kit', 'Set', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Labour', 'İş gücü', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ladder', 'Merdiven', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Landing', 'İniş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Landscape', 'Manzara', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lane', 'Lane', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Largely', 'Büyük ölçüde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lately', 'Son zamanlarda', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Launch', 'Öğle yemeği', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Leadership', 'Liderlik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('League', 'Lig', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lean', 'Eğilmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Legend', 'Efsane', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lens', 'Lens', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Licence', 'Lisans', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lifetime', 'Ömür boyu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lighting', 'Aydınlatma', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Likewise', 'Aynı şekilde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Limitation', 'Sınırlama', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Limited', 'Sınırlı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Literally', 'Gerçekten', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Literary', 'Edebi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Litter', 'Yavrulamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lively', 'Canlı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Load', 'Yük', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Loan', 'Borç', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Logical', 'Mantıksal', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Logo', 'Logo', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Long-term', 'Uzun vadeli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Loose', 'Gevşetmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lord', 'Lord', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lottery', 'Piyango', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lower', 'Daha düşük', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Loyal', 'Sadık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lung', 'Akciğer', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Lyric', 'Lirik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Magnificent', 'Muhteşem', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Maintain', 'Sürdürmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Majority', 'Çoğunluk', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Makeup', 'Makyaj yapmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Making', 'Yapım', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Manufacture', 'Üretmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Manufacturing', 'Üretme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Marathon', 'Maraton', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Margin', 'Marj', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Marker', 'İşaret', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Martial', 'Dövüş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mass', 'Yığın', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Massive', 'Cüsseli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Master', 'Usta', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Matching', 'Eşleşen', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mate', 'Mat', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Maximum', 'Maksimum', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Means', 'Araç', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Measurement', 'Ölçüm', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mechanic', 'Mekanik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mechanical', 'Mekanik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mechanism', 'Mekanizma', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Medal', 'Madalya', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Medication', 'İlaç tedavisi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Melt', 'Eritmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Membership', 'Üyelik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Memorable', 'Unutulmaz', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Metaphor', 'Metafor', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Military', 'Askeri', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Miner', 'Madenci', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mineral', 'Mineral', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Minimum', 'Minimum', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Minister', 'Bakan', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Minor', 'Küçük', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Minority', 'Azınlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Miserable', 'Acınası', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mission', 'Misyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mixed', 'Karışık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mode', 'Moda', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Modest', 'Mütevazı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Modify', 'Değiştirmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Monitor', 'İzlemek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Monster', 'Canavar', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Monthly', 'Aylık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Monument', 'Anıt', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Moral', 'Ahlak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Moreover', 'Dahası', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mortgage', 'İpotek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mosque', 'Cami', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mosquito', 'Sivrisinek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Motion', 'Hareket', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Motivate', 'Motive etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Motivation', 'Motivasyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Motor', 'Motor', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mount', 'Takılmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Moving', 'Hareketli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Multiple', 'Çoklu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Multiply', 'Çarpmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mysterious', 'Gizemli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Myth', 'Efsane', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Naked', 'Çıplak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Nasty', 'Edepsiz', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Navigation', 'Navigasyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Nearby', 'Yakında', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Neat', 'Düzenli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Necessity', 'Zorunluluk', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Negotiate', 'Müzakere etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Negotiation', 'Müzakere', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Nerve', 'Sinir', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Neutral', 'Doğal', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Nevertheless', 'Yine de', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Newly', 'Yeni', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Nickel', 'Nikel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Nightmare', 'Kabus', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Norm', 'Norm', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Notebook', 'Not defteri', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Notion', 'Fikir', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Novelist', 'Romancı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Nowadays', 'Bu günlerde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Numerous', 'Çeşitli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Nursing', 'Emzirme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Nutrition', 'Beslenme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Obesity', 'Obezite', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Obey', 'İtaat etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Objective', 'Nesne', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Obligation', 'Yükümlülük', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Observation', 'Gözlem', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Observe', 'Gözlemlemek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Observer', 'Gözlemci', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Obstacle', 'Engel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Obtain', 'Elde etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Occasionally', 'Ara sıra', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Occupation', 'Meslek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Occupy', 'İşgal etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Offence', 'Suç', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Offend', 'Rahatsız etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Offender', 'Suçlu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Offensive', 'Saldırgan', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ongoing', 'Devam eden', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Opening', 'Açılış', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Openly', 'Açıkça', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Opera', 'Opera', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Operate', 'Çalıştırmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Operator', 'Operatör', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Opponent', 'Rakip', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Oppose', 'Karşı çıkmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Opposed', 'Karşı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Opposition', 'Muhalefet', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Optimistic', 'İyimser', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Orchestra', 'Orkestra', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Organ', 'Organ', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Organic', 'Organik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Origin', 'Köken', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Otherwise', 'Aksi takdirde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Outcome', 'Sonuç', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Outer', 'Dış', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Outfit', 'Kıyafet', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Outline', 'Taslak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Output', 'Çıktı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Outstanding', 'Üstün', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Overall', 'Etraflı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Overcome', 'Üstesinden gelmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Overnight', 'Bir gece', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Owe', 'Sahip olmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ownership', 'Sahiplik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Oxygen', 'Oksijen', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pace', 'Adımlamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Packet', 'Paket', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Palm', 'Palmiye', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Panel', 'Panel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Panic', 'Panik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Parade', 'Geçit töreni', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Parallel', 'Paralel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Parliament', 'Parlamento', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Part-time/adv', 'Yarı zamanlı/adv', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Participant', 'Katılımcı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Participation', 'Katılım', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Partly', 'Kısmen', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Partnership', 'Ortaklık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Passage', 'Pasaj', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Passionate', 'Tutkulu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Password', 'Şifre', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Patch', 'Yama', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Patience', 'Sabır', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pause', 'Duraklamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Peer', 'Akran', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Penalty', 'Penaltı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pension', 'Emeklilik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Perceive', 'Algılamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Perception', 'Algı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Permanent', 'Kalıcı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Permanently', 'Kalıcı olarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Permit', 'İzin vermek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Perspective', 'Perspektif', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pharmacy', 'Eczane', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Phase', 'Faz', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Phenomenon', 'Fenomen', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Philosophy', 'Felsefe', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Physician', 'Doktor', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pile', 'Yığın', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pill', 'Hap', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pitch', 'Saha', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pity', 'Acımak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Placement', 'Atama', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Plain', 'Ova', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pointed', 'Sivri uçlu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Popularity', 'Popülerlik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Portion', 'Porsiyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pose', 'Poz', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Possess', 'Elinde bulundurmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Potential', 'Potansiyel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Potentially', 'Potansiyel olarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Praise', 'Övmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Precede', 'Öne çıkmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Precious', 'Değerli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Precise', 'Kesin', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Precisely', 'Açık olarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Predictable', 'Öngörülebilir', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Preference', 'Tercih', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pregnant', 'Hamile', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Preparation', 'Hazırlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Presence', 'Mevcudiyet', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Preserve', 'Korumak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Presidential', 'Başkanlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pride', 'Gurur', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Primarily', 'Öncelikle', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Prime', 'Astar vurmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Principal', 'Müdür', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Principle', 'İlke', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Prior', 'Önceki', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Priority', 'Öncelik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Privacy', 'Mahremiyet', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Probability', 'Olasılık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Probable', 'Olası', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Procedure', 'Prosedür', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Proceed', 'İlerlemek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Programming', 'Programlama', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Progressive', 'İlerici', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Prohibit', 'Yasaklamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Promising', 'Umut verici', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Promotion', 'Terfi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Prompt', 'Çabuk', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Proof', 'Kanıt', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Proportion', 'Oran', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Proposal', 'Teklif', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Propose', 'Önermek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Prospect', 'Olasılık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Protection', 'Koruma', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Protein', 'Protein', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Protester', 'Protestocu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Psychological', 'Psikolojik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Psychologist', 'Psikolog', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Psychology', 'Psikoloji', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Publication', 'Yayın', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Publicity', 'Tanıtım', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Publishing', 'Yayınlama', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Punk', 'Punk', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pupil', 'Öğrenci', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Purchase', 'Satın almak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pure', 'Saf', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Purely', 'Tamamen', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pursue', 'İzlemek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pursuit', 'Kovalama', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Puzzle', 'Bulmaca', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Questionnaire', 'Anket', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Racial', 'Irksal', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Racism', 'Irkçılık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Racist', 'Irkçı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Radiation', 'Radyasyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rail', 'Demiryolu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Random', 'Rastgele', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rank', 'Rütbe', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rapid', 'Ani', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rapidly', 'Hızlıca', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rat', 'Fare', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rating', 'Derecelendirme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Raw', 'Çiğ', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Realistic', 'Gerçekçi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Reasonable', 'Mantıklı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Reasonably', 'Makul bir şekilde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rebuild', 'Yeniden inşa etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Recall', 'Hatırlamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Receiver', 'Alıcı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Recession', 'Durgunluk', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Reckon', 'Tahmin temek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Recognition', 'Tanıma', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Recover', 'İyileşmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Recovery', 'İyileşmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Recruit', 'İyileştirmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Reduction', 'Kesinti', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Referee', 'Hakem', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Refugee', 'Mülteci', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Regard', 'Saygınlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Regional', 'Bölgesel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Register', 'Kayıt olmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Registration', 'Kayıt', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Regret', 'Pişmanlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Regulate', 'Düzenlemek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Regulation', 'Düzenleme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Reinforce', 'Güçlendirmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Relatively', 'Nispeten', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Relevant', 'İlgili', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Relief', 'Rahatlama', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Relieve', 'Rahatlamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Relieved', 'Rahatlamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rely', 'Güvenmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Remark', 'Söz', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Remarkable', 'Dikkat çekici', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Remarkably', 'Dikkate alınarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Reporting', 'Raporlama', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Representative', 'Temsilci', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Reputation', 'İtibar', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Requirement', 'Gereklilik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rescue', 'Kurtarmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Reserve', 'Rezerve etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Resident', 'İkamet eden kişi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Resign', 'İstifa etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Resist', 'Direnmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Resolution', 'Çözünürlük', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Resolve', 'Çözmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Resort', 'Tatil yeri', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Restore', 'Eski haline getirmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Restrict', 'Kısıtlamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Restriction', 'Kısıtlama', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Résumé', 'Sürdürmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Retail', 'Perakende', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Retain', 'Sürdürmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Retirement', 'Emeklilik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Reveal', 'Ortaya çıkarmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Revenue', 'Hasılat', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Revision', 'Revizyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Revolution', 'Devrim', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Reward', 'Ödül', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rhythm', 'Ritim', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rid', 'Kurtulmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ridiculous', 'Saçma', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Risky', 'Riskli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rival', 'Rakip', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rob', 'Soymak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Robbery', 'Soygun', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rocket', 'Roket', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Romance', 'Romantizm', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Root', 'Kök', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rose', 'Gül', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Roughly', 'Kabaca', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rub', 'Ovmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rubber', 'Lastik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ruin', 'Mahvetmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rural', 'Kırsal', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rush', 'Acele etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Satellite', 'Uydu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Satisfaction', 'Memnuniyet', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Satisfied', 'Memnun', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Satisfy', 'Tatmin etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Saving', 'Tasarruf', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Scale', 'Ölçek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Scandal', 'Skandal', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Scare', 'Korkutmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Scenario', 'Senaryo', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Scheme', 'Şema', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Scholar', 'Bilgin', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Scholarship', 'Burs', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Scratch', 'Çizik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Scream', 'Bağırmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Screening', 'Tarama', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sector', 'Sektör', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Secure', 'Güvenli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Seek', 'Aramak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Seeker', 'Arayan kimse', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Select', 'Seçme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Selection', 'Seçme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Self', 'Kendi kendine', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Seminar', 'Seminer', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Senior', 'Kıdemli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sensitive', 'Hassas', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sequence', 'Sekans', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Session', 'Oturum', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Settle', 'Yerleşmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Settler', 'Yerleşimci', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Severe', 'Haşin', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Severely', 'Şiddetle', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sexy', 'Seksi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shade', 'Gölge', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shadow', 'Gölge', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shallow', 'Sığ', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shame', 'Utanç', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shaped', 'Şekilli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shelter', 'Barınak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shock', 'Şok', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shocked', 'Şok', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shocking', 'Şok edici', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shooting', 'Çekim', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shore', 'Sahil', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Short-term', 'Kısa vadeli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shortage', 'Sıkıntı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shortly', 'Kısa süre içinde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Shot', 'Atış', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sibling', 'Kardeş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sidewalk', 'Kaldırım', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Signature', 'İmza', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Significance', 'Önem', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Significant', 'Önemli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Significantly', 'Önemli ölçüde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Silence', 'Sessizlik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Silk', 'İpek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sincere', 'İçten', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Skilled', 'Yetenekli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Skull', 'Kafatası', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Slave', 'Köle', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Slide', 'Slayt', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Slight', 'Hafif', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Slip', 'Kaymak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Slogan', 'Slogan', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Slope', 'Yamaç', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('So-called', 'Sözde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Solar', 'Güneş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Somehow', 'Bir şekilde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sometime', 'Bir ara', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Somewhat', 'Biraz', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sophisticated', 'Komplike', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Soul', 'Ruh', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Spare', 'Kıyamamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Specialist', 'Uzman', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Specialise', 'Uzmanlaşmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Species', 'Türler', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Specify', 'Belirtmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Spectacular', 'Muhteşem', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Spectator', 'Seyirci', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Speculate', 'Spekülasyon yapmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Speculation', 'Spekülasyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Spice', 'Baharat', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Spill', 'Dökülme', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Spiritual', 'Manevi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Spite', 'Kınama', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Split', 'Bölmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Spoil', 'Şımartmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Spokesman', 'Sözcü', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Spokesperson', 'Sözcü', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Spokeswoman', 'Sözcü', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sponsor', 'Sponsor', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sponsorship', 'Sponsorluk', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stable', 'Stabil', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stall', 'Durak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stance', 'Duruş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stare', 'Dik dik dikmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Starve', 'Açlıktan ölmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Status', 'Durum', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Steadily', 'İstikrarlı bir şekilde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Steady', 'Sabit durmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Steam', 'Buhar', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Steel', 'Çelik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Steep', 'Dik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sticky', 'Yapışkan', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stiff', 'Sert', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stimulate', 'Teşvik etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stock', 'Stoklamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stream', 'Aktarım', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Strengthen', 'Güçlendirmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stretch', 'Uzatmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Strict', 'Sıkı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Strictly', 'Kesin olarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Strike', 'Çarpmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Striking', 'Çarpıcı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stroke', 'Felç', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Struggle', 'Çabalamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Stunning', 'Çekici', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Submit', 'Göndermek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Subsequent', 'Sonraki', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Subsequently', 'Daha sonra', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Suburb', 'Banliyö', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Suffering', 'Cefa', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sufficient', 'Yeterli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sufficiently', 'Yeterince', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sum', 'Meblağ', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Super', 'Süper', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Surgeon', 'Cerrah', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Surgery', 'Ameliyat', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Surround', 'Çevrelemek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Surrounding', 'Çevreleyen', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Survival', 'Hayatta kalma', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Survivor', 'Kurtulan', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Suspect', 'Şüphelenmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Suspend', 'Askıya almak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sustainable', 'Sürdürülebilir', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Swallow', 'Yutmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Swear', 'Küfür', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sweep', 'Süpürmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sympathetic', 'Sempatik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sympathy', 'Sempati', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tag', 'Etiket', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tale', 'Masal', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tank', 'Tank', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tap', 'Musluk', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tear', 'Göz yaşı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Technological', 'Teknolojik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Teen', 'Genç', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Temple', 'Tapınak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Temporarily', 'Geçici olarak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Temporary', 'Geçici', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tendency', 'Eğilim', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tension', 'Tansiyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Terminal', 'Terminal', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Terms', 'Şartlar', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Terribly', 'Çok', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Terrify', 'Korkutmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Territory', 'Bölge', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Terror', 'Terör', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Terrorism', 'Terörizm', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Terrorist', 'Terörist', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Testing', 'Test', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Textbook', 'Ders kitabı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Theft', 'Hırsızlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Therapist', 'Terapist', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Therapy', 'Terapi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Thesis', 'Tez', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Thorough', 'Tam', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Thoroughly', 'İyice', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Threat', 'Tehdit', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Threaten', 'Tehdit etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Thumb', 'Baş parmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Thus', 'Böylece', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Timing', 'Zamanlama', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tissue', 'Doku', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tone', 'Tonlamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tough', 'Zorlu', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tournament', 'Turnuva', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Trace', 'İz', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Trading', 'Ticaret', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tragedy', 'Trajedi', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tragic', 'Trajik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Trait', 'Karakter', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Transfer', 'Transfer etmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Transform', 'Dönüşüm', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Transition', 'Geçiş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Transmit', 'Aktarmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Trap', 'Tuzak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Treasure', 'Hazine', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Trial', 'Duruşma', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tribe', 'Kabile', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Trigger', 'Tetiklemek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Trillion', 'Trilyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Troop', 'Birleşmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tropical', 'Tropikal', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Truly', 'Tamamen', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Trust', 'Güven', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tsunami', 'Tsunami', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tune', 'Ayarlamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tunnel', 'Tünel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ultimate', 'Nihai', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ultimately', 'Nihayetinde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Unacceptable', 'Kabul edilemez', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Uncertainty', 'Belirsizlik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Unconscious', 'Bilinçsiz', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Undergo', 'Başlamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Undertake', 'Üstlenmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Unexpected', 'Beklenmedik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Unfold', 'Açılmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Unfortunate', 'Talihsiz', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Unique', 'Eşsiz', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Unite', 'Birleştirmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Unity', 'Birlik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Universal', 'Evrensel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Universe', 'Evren', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Unknown', 'Bilinmeyen', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Upper', 'Üst', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Upwards', 'Yukarı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Urban', 'Kentsel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Urge', 'Dürtü', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Urgent', 'Acil', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Usage', 'Kullanım', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Useless', 'Kullanışsız', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Valid', 'Geçerli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Variation', 'Varyasyon', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Vary', 'Çeşitli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Vast', 'Büyüklük', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Venue', 'Yer', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Vertical', 'Dikey', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Via', 'Aracılığıyla', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Victory', 'Zafer', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Viewpoint', 'Bakış açısı', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Violence', 'Şiddet', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Virtual', 'Sanal', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Visa', 'Visa', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Visible', 'Görünür', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Vision', 'Görüş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Visual', 'Görsel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Vital', 'Hayati', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Vitamin', 'Vitamin', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Volume', 'Hacim', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Voluntary', 'Gönüllü', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Voting', 'Oylama', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wage', 'Maaş', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wander', 'Dolaşmak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Warming', 'Isınma', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Weakness', 'Zayıflık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wealth', 'Varlık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wealthy', 'Zengin', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Weekly', 'Haftalık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Weird', 'Garip', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Welfare', 'Refah', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wheat', 'Buğday', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Whereas', 'Halbuki', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wherever', 'Her yerde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Whisper', 'Fısıldamak', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Whoever', 'Kimse', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Whom', 'Kime', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Widely', 'Geniş bir şekilde', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Widespread', 'Yaygın', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wildlife', 'Vahşi yaşam', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Willing', 'Hevesli', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wire', 'Tel', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wisdom', 'Bilgelik', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wise', 'Bilge', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Withdraw', 'Geri çekilmek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Witness', 'Tanık', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wolf', 'Kurt', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Workforce', 'İşgücü', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Workplace', 'İşyeri', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Workshop', 'Atölye', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Worm', 'Solucan', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wound', 'Yara', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wrap', 'Dürüm', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Wrist', 'Bilek', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Zone', 'Alan', 'B2', NULL, 0, 0);
INSERT INTO words
VALUES ('Abolish', 'Ortadan kaldırmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Abortion', 'Kürtaj', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Absence', 'Yokluk', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Absent', 'Mevcut olmayan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Absurd', 'Saçma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Abuse', 'Suistimal etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Academy', 'Akademi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Accelerate', 'Hızlanmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Acceptance', 'Kabul', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Accessible', 'Erişilebilir', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Accomplishment', 'Başarı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Accordingly', 'Buna göre', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Accountability', 'Hesap verebilirlik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Accountable', 'Sorumlu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Accumulate', 'Biriktirmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Accumulation', 'Birikim', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Accusation', 'Suçlama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Accused', 'Sanık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Acid', 'Asit', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Acquisition', 'Kazanma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Activation', 'Aktivasyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Activist', 'Aktivist', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Acute', 'Akut', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Adaptation', 'Uyarlama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Adhere', 'Uymak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Adjacent', 'Bitişik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Adjustment', 'Ayarlama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Administer', 'İdare etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Administrative', 'İdari', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Administrator', 'Yönetici', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Admission', 'Giriş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Adolescent', 'Ergen', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Adoption', 'Evlat edinme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Adverse', 'Olumsuz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Advocate', 'Savunucu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Aesthetic', 'Estetik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Affection', 'Alaka', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Aftermath', 'Sonradan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Aggression', 'Saldırı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Agricultural', 'Tarımsal', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Aide', 'Yardımcı olmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Alert', 'Uyarı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Align', 'Hizalamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Alignment', 'Uyuşma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Alike', 'Aynı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Allegation', 'İddia', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Allege', 'İddia etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Allegedly', 'İddia edilen', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Alliance', 'İttifak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Allocate', 'Tahsis etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Allocation', 'Paylaştırma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Allowance', 'Ödenek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ally', 'Müttefik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Aluminum', 'Alüminyum', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Amateur', 'Amatör', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ambassador', 'Büyükelçi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Amend', 'İyileştirmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Amendment', 'Değişiklik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Amid', 'Ortasında', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Analogy', 'Benzetme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Anchor', 'Ankra', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Angel', 'Melek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Anonymous', 'Anonim', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Apparatus', 'Aparat', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Apparel', 'Giyim', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Appealing', 'Çekici', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Appetite', 'İştah', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Applaud', 'Alkışlamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Applicable', 'Uygulanabilir', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Appoint', 'Atamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Appreciation', 'Takdir', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Arbitrary', 'Keyfi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Architectural', 'Mimari', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Archive', 'Arşiv', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Arena', 'Arena', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Arm (v)', 'Kol (v)', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Array', 'Sıralamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Articulate', 'İfade etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ash', 'Kül', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Aspiration', 'İstek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Aspire', 'İstemek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Assassination', 'Suikast', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Assault', 'Saldırı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Assemble', 'Birleştirmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Assembly', 'Toplantı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Assert', 'İddia etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Assertion', 'İddia', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Assurance', 'Güvence', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Asylum', 'İltica', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Atrocity', 'Vahşet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Attain', 'Elde etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Attendance', 'Katılma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Attribute', 'Bağlanmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Auction', 'Açık artırma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Audit', 'Denetim', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Authentic', 'Otantik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Authorize', 'Yetki vermek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Auto', 'Otomobil', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Autonomy', 'Özerklik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Availability', 'Kullanılabilirlik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Await', 'Beklemek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Backdrop', 'Zemin', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Backing', 'Destek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Backup', 'Yedekleme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bail', 'Kefalet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ballot', 'Oy pusulası', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bankruptcy', 'İflas', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Banner', 'Afiş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bare', 'Çıplak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Barrel', 'Varil', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bass', 'Bas', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Battlefield', 'Savaş alanı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bay', 'Koy', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Beam', 'Kiriş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Beast', 'Canavar', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Behalf', 'Adına', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Behavioral', 'Davranışsal', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Beloved', 'Sevgili', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bench', 'Tezgâh', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Benchmark', 'Kalite testi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Beneath', 'Altında', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Beneficiary', 'Yararlanma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Betray', 'İhanet etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Beverage', 'İçecek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bind', 'Bağırmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Biography', 'Biyografi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bishop', 'Piskopos', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bizarre', 'Tuhaf', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Blade', 'Bıçak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Blast', 'Büyük patlama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bleed', 'Kanama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Blend', 'Karışım', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bless', 'Kutsamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Blessing', 'Nimet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Boast', 'Övünme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bonus', 'Bonus', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Booking', 'Rezervasyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Boom', 'Patlama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bounce', 'Sıçrama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Boundary', 'Sınır', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bow', 'Yay', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Breach', 'Çiğneme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Breakdown', 'Bozulma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Breakthrough', 'Atılım', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Breed', 'Yavrulamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Broadband', 'Geniş bant', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Browser', 'Tarayıcı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Brutal', 'Vahşi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Buddy', 'Ahbap', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Buffer', 'Tampon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bulk', 'Toplu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Burden', 'Yük', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Bureaucracy', 'Bürokrasi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Burial', 'Gömme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Burst', 'Patlamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cabinet', 'Kabine', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Calculation', 'Hesaplama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Canvas', 'Tuval', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Capability', 'Kabiliyet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Capitalism', 'Kapitalizm', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Capitalist', 'Kapitalist', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cargo', 'Kargo', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Carriage', 'Taşıma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Carve', 'Oymak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Casino', 'Kumarhane', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Casualty', 'Yaralı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Catalog', 'Katalog', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cater', 'Hitap etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cattle', 'Sığır', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Caution', 'Dikkat', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cautious', 'Dikkatli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cease', 'Durdurmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cemetery', 'Mezarlık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Chamber', 'Oda', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Chaos', 'Kaos', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Characterise', 'Karakterize etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Charm', 'Cazibe', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Charter', 'Tüzük', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Choir', 'Koro', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Chronic', 'Kronik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Chunk', 'Yastık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Circulate', 'Dolaşmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Circulation', 'Dolaşım', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Citizenship', 'Vatandaşlık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Civic', 'Sivil', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Civilian', 'Sivil', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Clarity', 'Açıklık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Clash', 'Çarpışma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Classification', 'Sınıflandırma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cling', 'Yapışan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Clinical', 'Klinik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Closure', 'Kapanma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cluster', 'Küme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Coalition', 'Koalisyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Coastal', 'Kıyı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cocktail', 'Kokteyl', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cognitive', 'Bilişsel', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Coincide', 'Çakmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Collaborate', 'İşbirliği yapmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Collaboration', 'İşbirliği', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Collective', 'Toplu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Collision', 'Çarpışma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Colonial', 'Sömürgeci', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Columnist', 'Köşe yazarı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Combat', 'Savaşmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Commence', 'Başlamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Commentary', 'Yorum', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Commentator', 'Yorumcu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Commerce', 'Ticaret', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Commissioner', 'Komiser', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Commodity', 'Emtia', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Communist', 'Komünist', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Companion', 'Arkadaş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Comparable', 'Karşılaştırılabilir', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Compassion', 'Merhamet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Compel', 'Zorlamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Compelling', 'Zorlayıcı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Compensate', 'Telâfi etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Compensation', 'Tazminat', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Competence', 'Yeterlik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Competent', 'Yetkili', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Compile', 'Derlemek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Complement', 'Tamamlayıcı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Complexity', 'Karmaşıklık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Compliance', 'Uygunluk', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Complication', 'Komplikasyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Comply', 'Uymak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Composition', 'Kompozisyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Compromise', 'Anlaşmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Compute', 'Hesaplamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Conceal', 'Gizlemek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Concede', 'Kabul etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Conceive', 'Gebe kalmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Conception', 'Gebe kalma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Concession', 'İmtiyaz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Condemn', 'Kınamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Confer', 'Vermek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Confession', 'İtiraf', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Configuration', 'Konfigürasyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Confine', 'Sınırlamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Confirmation', 'Onaylama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Confront', 'Yüzleşmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Confrontation', 'Yüzleşme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Congratulate', 'Tebrik etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Congregation', 'Cemaat', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Congressional', 'Kongre', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Conquer', 'Fethetmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Conscience', 'Vicdan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Consciousness', 'Bilinç', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Consecutive', 'Ardışık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Consensus', 'Fikir birliği', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Consent', 'Onay', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Conserve', 'Korumak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Consistency', 'Tutarlılık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Consolidate', 'Birleştirmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Constitute', 'Oluşturmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Constitution', 'Anayasa', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Constitutional', 'Anayasal', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Constraint', 'Kısıtlama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Consultation', 'Danışma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Contemplate', 'Tasarlamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Contempt', 'Hor görme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Contend', 'İtiraz etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Contender', 'Yarışmacı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Contention', 'Çekişme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Continually', 'Sürekli olarak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Contractor', 'Yüklenici', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Contradiction', 'Çelişki', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Contrary', 'Zıt', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Contributor', 'Katkıda bulunan kimse', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Conversion', 'Dönüşüm', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Convict', 'Mahkum etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Conviction', 'Mahkumiyet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cooperate', 'İşbirliği yapmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cooperative', 'İşbirliği ile ilgili', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Coordinate', 'Koordinat', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Coordination', 'Koordinasyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Coordinator', 'Koordinatör', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Copper', 'Bakır', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Copyright', 'Telif hakkı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Correction', 'Düzeltme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Correlate', 'Korelasyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Correlation', 'Korelasyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Correspond', 'Karşılık vermek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Correspondence', 'Yazışma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Correspondent', 'Muhabir', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Corresponding', 'Karşılıklı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Corrupt', 'Yozlaşmış', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Corruption', 'Yolsuzluk', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Costly', 'Pahalı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Councilor', 'Meclis üyesi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Counseling', 'Danışmanlık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Counselor', 'Danışman', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Counter (argue)', 'Sayaç (tartış)', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Counterpart', 'Muamele', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Countless', 'Sayısız', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Coup', 'Darbe', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Courtesy', 'Nezaket', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Craft', 'Zanaat', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Crawl', 'Sürünmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Creator', 'Yaratıcı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Credibility', 'Güvenilirlik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Credible', 'Güvenilir', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Creep', 'Sürünmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Critique', 'Eleştiri', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Crown', 'Taç', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Crude', 'Ham', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Crush', 'Ezmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Crystal', 'Kristal', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cult', 'Kült', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cultivate', 'Yetiştirmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Curiosity', 'Merak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Custody', 'Velayet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cutting', 'Kesme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Cynical', 'Alaycı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dam', 'Baraj', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Damaging', 'Zararlı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dawn', 'Şafak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Debris', 'Enkaz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Debut', 'Çıkış', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Decision-making', 'Karar verme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Decisive', 'Kararlı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Declaration', 'Beyanname', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dedicated', 'Özel', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dedication', 'Adanmışlık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Deed', 'Tapu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Deem', 'Kabul etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Default', 'Varsayılan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Defect', 'Kusur', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Defensive', 'Savunmacı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Deficiency', 'Eksiklik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Deficit', 'Açıklık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Defy', 'Meydan okumak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Delegate', 'Temsilci', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Delegation', 'Delegasyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Delicate', 'Narin', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Demon', 'İblis', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Denial', 'İnkar', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Denounce', 'Kınamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dense', 'Yoğun', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Density', 'Yoğunluk', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dependence', 'Bağımlılık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Depict', 'Tasvir etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Deploy', 'Dağıtmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Deployment', 'Dağıtım', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Deprive', 'Mahrum etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Deputy', 'Milletvekili', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Descend', 'İnmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Descent', 'İniş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Designate', 'Belirlemek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Desirable', 'İstenen', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Desktop', 'Masaüstü', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Destructive', 'Yıkıcı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Detain', 'Gözaltına almak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Detection', 'Tespit', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Detention', 'Alıkoyma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Deteriorate', 'Bozulmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Devastate', 'Harap etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Devil', 'Şeytan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Devise', 'Tasarlamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Diagnose', 'Teşhis etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Diagnosis', 'Teşhis', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dictate', 'Dikte', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dictator', 'Diktatör', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Differentiate', 'Ayırt etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dignity', 'İtibar', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dilemma', 'İkilem', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dimension', 'Boyut', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Diminish', 'Azaltmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dip', 'Dalmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Diplomat', 'Diplomat', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Diplomatic', 'Diplomatik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Directory', 'Dizin', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Disastrous', 'Felaket', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Discard', 'Atmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Discharge', 'Boşaltma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Disclose', 'İfşa etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Disclosure', 'Açıklama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Discourse', 'Söylem', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Discretion', 'Takdir', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Discrimination', 'Ayrımcılık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dismissal', 'İşten çıkarma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Displace', 'Yerinden çıkarmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Disposal', 'İmha etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dispose', 'Elden çıkarmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dispute', 'Anlaşmazlık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Disrupt', 'Bozulmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Disruption', 'Bozulma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dissolve', 'Çözmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Distinction', 'Ayrım', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Distinctive', 'Ayırt edici', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Distort', 'Çarpıtmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Distress', 'Sıkıntı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Disturbing', 'Rahatsız edici', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Divert', 'Saptırmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Divine', 'İlahi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Doctrine', 'Doktrin', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Documentation', 'Belgeler', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Domain', 'İhtisas', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dominance', 'Hakimiyet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Donor', 'Donör', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dose', 'Doz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Drain', 'Boşaltmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Drift', 'Sürüklenme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Drown', 'Boğulmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dual', 'Çift', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dub', 'Dublaj', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Dumb', 'Sersem', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Duo', 'İkili', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Earnings', 'Kazanç', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ease', 'Kolaylaştırmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Echo', 'Yankı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ecological', 'Ekolojik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Educator', 'Eğitimci', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Effectiveness', 'Verimlilik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Efficiency', 'Yeterlik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ego', 'Ego', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Elaborate', 'Detaylandırmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Electoral', 'Seçim', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Elevate', 'Yükseltmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Eligible', 'Hak sahibi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Elite', 'Seçkin', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Embark', 'Başlatmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Embarrassment', 'Utanç', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Embassy', 'Elçilik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Embed', 'Gömmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Embody', 'Somutlaştırmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Emergence', 'Ortaya çıkma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Empirical', 'Ampirik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Empower', 'Güçlendirmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Enact', 'Sahnelemek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Encompass', 'Kapsamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Encouragement', 'Cesaret', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Encouraging', 'Cesaret verici', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Endeavor', 'Çaba', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Endless', 'Sonsuz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Endorse', 'Onaylamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Endorsement', 'Onaylama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Endure', 'Dayanmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Enforce', 'Zorlamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Enforcement', 'Uygulama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Engagement', 'Nişanlanmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Engaging', 'İlgi çekici', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Enrich', 'Zenginleştirmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Enroll', 'Kaydetmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ensue', 'Ortaya çıkmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Enterprise', 'Girişim', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Enthusiast', 'Meraklı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Entitle', 'Hak kazanmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Entity', 'Varlık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Epidemic', 'Epidemi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Equality', 'Eşitlik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Equation', 'Denklem', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Erect', 'Dik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Escalate', 'Tırmanmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Essence', 'Öz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Establishment', 'Kuruluş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Eternal', 'Sonsuz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Evacuate', 'Tahliye ediyorum', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Evoke', 'Uyandırmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Evolutionary', 'Evrimsel', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Exaggerate', 'Abartmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Excellence', 'Mükemmellik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Exceptional', 'Olağanüstü', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Excess', 'Aşırı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Exclusion', 'Hariç tutma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Exclusive', 'Özel', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Exclusively', 'Sadece', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Execute', 'Uygulamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Execution', 'Uygulamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Exert', 'Zorlamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Exile', 'Sürgün', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Expenditure', 'Harcama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Experimental', 'Deneysel', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Expire', 'Süresi dolmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Explicit', 'Açık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Explicitly', 'Açıkça', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Exploitation', 'Sömürme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Explosive', 'Patlayıcı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Extremist', 'Aşırı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Facilitate', 'Kolaylaştırmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Faction', 'Hizip', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fade', 'Solmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fairness', 'Adillik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fatal', 'Ölümcül', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fate', 'Kader', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Favorable', 'Elverişli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Feat', 'Başarı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Felony', 'Suç', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Feminist', 'Feminist', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fiber', 'Fiber', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fierce', 'Şiddetli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Filmmaker', 'Film yapımcısı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Filter', 'Filtre', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Firearm', 'Ateşli silah', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fiscal', 'Mali', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Flaw', 'Kusur', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Flawed', 'Kusurlu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Flee', 'Kaçmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fleet', 'Filo', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Flesh', 'Beden', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Flexibility', 'Esneklik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Flourish', 'Güzelleşmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fluid', 'Sıvı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Footage', 'Çekim', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Foreigner', 'Yabancı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Forge', 'Dövmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Formula', 'Formül', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Formulate', 'Formüle etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Forth', 'İleri', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Forthcoming', 'Yaklaşan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Foster', 'Bakmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fragile', 'Kırılgan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Franchise', 'Franchise', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Frankly', 'Açıkçası', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Freshman', 'Birinci sınıf öğrencisi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Frustrated', 'Hüsrana uğramış', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Frustrating', 'Sinir bozucu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Frustration', 'Hayal kırıklığı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Functional', 'İşlevsel', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Fundraising', 'Bağış toplama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Funeral', 'Cenaze', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Gambling', 'Kumar', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Gathering', 'Toplama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Gaze', 'Bakış', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Gear', 'Vites', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Generic', 'Genel', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Genocide', 'Soykırım', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Gig', 'Konser vermek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Glance', 'Bakış atmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Glimpse', 'Göz atmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Glorious', 'Görkemli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Glory', 'Görkem', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Governance', 'Yönetişim', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Grace', 'Lütuf', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Grasp', 'Sıkı sıkı tutmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Grave (cemetery)', 'Mezar (mezarlık)', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Grave (serious)', 'Mezar (ciddi)', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Gravity', 'Yer çekimi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Grid', 'Izgara', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Grief', 'Yas', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Grin', 'Sırıtış', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Grind', 'Öğütmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Grip', 'Kavramak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Gross', 'Brüt', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Guerrilla', 'Gerilla', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Guidance', 'Rehberlik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Guilt', 'Suçluluk', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Gut', 'Bağırsak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hail', 'Dolu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Halfway', 'Yarıya kadar', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Halt', 'Durma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Handful', 'Avuç', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Handling', 'İşleme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Handy', 'Kullanışlı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Harassment', 'Taciz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hardware', 'Donanım', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Harmony', 'Uyum', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Harsh', 'Sert', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Harvest', 'Hasat', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hatred', 'Nefret', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Haunt', 'Uğrak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hazard', 'Tehlike', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Heighten', 'Yükseltmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Heritage', 'Miras', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hierarchy', 'Hiyerarşi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('High-profile', 'Yüksek profilli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hint', 'İpucu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Homeland', 'Anavatan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hopeful', 'Umutlu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Horizon', 'Ufuk', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Horn', 'Korna', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hostage', 'Rehin', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hostile', 'Düşmanca', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hostility', 'Düşmanlık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Humanitarian', 'İnsani', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Humanity', 'İnsanlık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Humble', 'Mütevazı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Hydrogen', 'Hidrojen', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Identification', 'Tanılama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ideological', 'İdeolojik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ideology', 'İdeoloji', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Idiot', 'Salak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ignorance', 'Cehalet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Imagery', 'Görüntüler', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Immense', 'Engin', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Imminent', 'Yakın', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Implementation', 'Uygulama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Imprison', 'Hapsetmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Inability', 'Yetersizlik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Inadequate', 'Yetersiz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Inappropriate', 'Uygunsuz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Incarcerate', 'Hapsetmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Incarceration', 'Hapsetme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Incidence', 'İnsidans', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Inclined', 'Eğimli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Inclusion', 'Dahil etme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Incur', 'Katlanmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Indicator', 'Gösterge', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Indictment', 'İddianame', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Indigenous', 'Yerli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Induce', 'İndüklemek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Indulge', 'Şımartmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Inequality', 'Eşitsizlik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Infamous', 'Rezil', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Infant', 'Bebek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Infect', 'Enfekte etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Inflict', 'Zorlamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Influential', 'Etkili', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Inherent', 'Doğal', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Inhibit', 'Engellemek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Initiate', 'Başlatmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Inject', 'Enjekte etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Injection', 'Enjeksiyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Injustice', 'Haksızlık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Inmate', 'Mahkum', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Inquire', 'Sormak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Insertion', 'Ekleme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Insider', 'İçeriden', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Inspect', 'İncelemek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Inspection', 'Denetleme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Inspiration', 'Esin', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Instinct', 'İçgüdü', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Institutional', 'Kurumsal', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Instruct', 'Talimat vermek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Instrumental', 'Enstrümantal', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Insufficient', 'Yetersiz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Insult', 'Hakaret', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Intact', 'Bozulmamış', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Intake', 'Alım', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Integral', 'İntegral', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Integrated', 'Entegre', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Integration', 'Entegrasyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Integrity', 'Bütünlük', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Intensify', 'Yoğunlaştırmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Intensity', 'Yoğunluk', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Intensive', 'Yoğun', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Intent', 'Niyet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Interactive', 'Etkileşimli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Interface', 'Arayüz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Interfere', 'Müdahale etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Interference', 'Parazit yapmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Interim', 'Ara', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Interior', 'İç mekan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Intermediate', 'Orta seviye', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Intersection', 'Kavşak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Intervene', 'Müdahale etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Intervention', 'Araya girmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Intimate', 'Samimi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Intriguing', 'İlgi çekici', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Inventory', 'Envanter', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Investigator', 'Müfettiş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Invisible', 'Görünmez', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Invoke', 'Çağırmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Involvement', 'Tutulum', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ironic', 'İronik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ironically', 'İronik bir şekilde', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Irony', 'İroni', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Irrelevant', 'Alakasız', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Isolation', 'İzolasyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Judicial', 'Yargı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Jurisdiction', 'Yargı yetkisi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Justification', 'Gerekçe', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Kidnap', 'Kaçırmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Kidney', 'Böbrek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Kingdom', 'Krallık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Landlord', 'Ev sahibi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Landmark', 'Dönüm noktası', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lap', 'Kucak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Large-scale', 'Büyük ölçekli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Laser', 'Lazer', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Latter', 'İkincisi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lawmaker', 'Milletvekili', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lawn', 'Çim', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lawsuit', 'Dava', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Layout', 'Düzen', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Leak', 'Sızıntı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Leap', 'Sıçramak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Legacy', 'Miras', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Legendary', 'Efsanevi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Legislation', 'Mevzuat', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Legislative', 'Yasama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Legislature', 'Yasama organı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Legitimate', 'Meşru', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lengthy', 'Uzun', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lesbian', 'Lezbiyen', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lesser', 'Daha az', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lethal', 'Ölümcül', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Liable', 'Sorumlu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Liberal', 'Liberal', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Liberation', 'Kurtuluş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Liberty', 'Özgürlük', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lifelong', 'Ömür boyu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Likelihood', 'Olasılık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Limb', 'Uzuv', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Linear', 'Doğrusal', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lineup', 'Sıralanmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Linger', 'Oyalanmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Listing', 'Liste', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Liter', 'Litre', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Literacy', 'Okuryazarlık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Liver', 'Karaciğer', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Lobby', 'Lobi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Log', 'Kayıt', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Logic', 'Mantık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Long-standing', 'Uzun süredir devam eden', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Longtime', 'Uzun zamandır', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Loom', 'Tezgâh', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Loop', 'Döngü', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Loyalty', 'Bağlılık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Machinery', 'Makine', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Magical', 'Büyülü', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Magnetic', 'Manyetik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Magnitude', 'Büyüklük', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mainland', 'Anakara', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mainstream', 'Ana akım', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Maintenance', 'Bakım', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mandate', 'Hakimiyet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mandatory', 'Zorunlu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Manifest', 'Tezahür etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Manipulate', 'Manipüle etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Manipulation', 'Manipülasyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Manuscript', 'El yazması', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Marginal', 'Marjinal', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Marine', 'Deniz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Marketplace', 'Pazar', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mask', 'Maske', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Massacre', 'Katliam', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mathematical', 'Matematiksel', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mature', 'Olgun', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Maximize', 'En üst düzeye çıkarmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Meaningful', 'Anlamlı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Meantime', 'Bu arada', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Medieval', 'Ortaçağ', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Meditation', 'Meditasyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Melody', 'Melodi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Memo', 'Hafıza', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Memoir', 'Anı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Memorial', 'Anıt', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mentor', 'Akıl hocası', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Merchant', 'Tüccar', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mercy', 'Merhamet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mere', 'Sadece', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Merely', 'Sadece', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Merge', 'Birleştirmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Merger', 'Birleşme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Merit', 'Hak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Methodology', 'Metodoloji', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Midst', 'Ortasında', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Migration', 'Göç', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Militant', 'Militan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Militia', 'Milis', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mill', 'Değirmen', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Minimal', 'Minimal', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Minimize', 'En aza indirmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mining', 'Madencilik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ministry', 'Bakanlık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Miracle', 'Mucize', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Misery', 'Sefalet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Misleading', 'Yanıltıcı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Missile', 'Füze', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mob', 'Mafya', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mobility', 'Hareketlilik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mobilize', 'Harekete geçirmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Moderate', 'Ilıman', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Modification', 'Değiştirme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Module', 'Modül', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Momentum', 'İvme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Monk', 'Keşiş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Monopoly', 'Tekel', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Morality', 'Ahlak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Motive', 'Güdü', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Municipal', 'Belediye', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Mutual', 'Karşılıklı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Namely', 'Yani', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nationwide', 'Ülke çapında', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Naval', 'Deniz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Neglect', 'İhmal etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Neighboring', 'Komşu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nest', 'Yuva', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Newsletter', 'Bülten', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Niche', 'Niş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Noble', 'Soylu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nod', 'Başını sallamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nominate', 'Aday göstermek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nomination', 'Adaylık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nominee', 'Aday', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nonetheless', 'Yine de', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nonprofit', 'Kar amacı gütmeyen kuruluş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nonsense', 'Anlamsız', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Noon', 'Öğlen', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Notable', 'Dikkate değer', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Notably', 'Özel olarak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Notify', 'Bilgilendirmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Notorious', 'Kötü şöhretli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Nursery', 'Kreş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Objection', 'İtiraz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Oblige', 'Yükümlemek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Obsess', 'Takıntılı olmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Obsession', 'Saplantı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Occasional', 'Ara sıra', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Occurrence', 'Ortaya çıkma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Odds', 'İhtimal', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Offering', 'Teklif', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Offspring', 'Yavru', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Operational', 'Operasyonel', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Opt', 'Seçmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Optical', 'Optik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Optimism', 'İyimserlik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Oral', 'Sözlü', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Organisational', 'Örgütsel', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Orientation', 'Oryantasyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Originate', 'Doğmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Outbreak', 'Salgın', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Outing', 'Gezi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Outlet', 'Çıkış', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Outlook', 'Görünüm', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Outrage', 'Öfke', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Outsider', 'Yabancı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Overlook', 'Gözden kaçmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Overly', 'Aşırı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Oversee', 'Denetlemek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Overturn', 'Devirmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Overwhelm', 'Ezmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Overwhelming', 'Ezici', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pad', 'Ped', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Parameter', 'Parametre', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Parental', 'Ebeveyn', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Partial', 'Kısmi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Partially', 'Kısmen', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Passing', 'Geçme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Passive', 'Pasif', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pastor', 'Papaz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Patent', 'Patent', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pathway', 'Yol', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Patrol', 'Devriye', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Patron', 'Patron', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Peak', 'Doruğa ulaşmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Peasant', 'Köylü', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Peculiar', 'Özel eşya', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Persist', 'Israr etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Persistent', 'Kalıcı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Personnel', 'Personel', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Petition', 'Dilekçe', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Philosopher', 'Filozof', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Philosophical', 'Felsefi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pioneer', 'Öncü', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pipeline', 'Boru hattı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pirate', 'Korsan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pit', 'Çukur', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Plea', 'Savunma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Plead', 'Savunmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pledge', 'Taahhüt', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Plug', 'Fiş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Plunge', 'Dalma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pole', 'Direk', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Poll', 'Anket', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pond', 'Gölet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Portfolio', 'Portföy', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Portray', 'Tasvir etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Postpone', 'Ertelemek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Postwar', 'Savaş sonrası', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Practitioner', 'Uygulayıcı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Preach', 'Vaaz vermek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Precedent', 'Emsal teşkil eden', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Precision', 'Kesinlik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Predator', 'Yırtıcı kimse', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Predecessor', 'Selefi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Predominantly', 'Ağırlıklı olarak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pregnancy', 'Gebelik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prejudice', 'Ön yargı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Preliminary', 'Ön', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Premier', 'Başbakan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Premise', 'Öncül', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Premium', 'Prim', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prescribe', 'Reçete etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prescription', 'Reçete', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Presently', 'Şu anda', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Preservation', 'Koruma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Preside', 'Başkanlık etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Presidency', 'Başkanlık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prestigious', 'Prestijli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Presumably', 'Muhtemelen', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Presume', 'Varsaymak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prevail', 'Hakim olmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prevalence', 'Yaygınlık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prevention', 'Önleme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prey', 'Kurban', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Privatization', 'Özelleştirme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Privilege', 'Ayrıcalık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Probe', 'Sondaj', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Problematic', 'Sorunlu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Proceeding', 'İlerleme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Proceeds', 'Gelir', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Processing', 'İşleme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Processor', 'İşlemci', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Proclaim', 'İlan etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Productive', 'Üretken', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Productivity', 'Verimlilik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Profitable', 'Kârlı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Profound', 'Engin', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Projection', 'Projeksiyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prominent', 'Öne çıkan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pronounced', 'Belirgin', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Propaganda', 'Propaganda', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Proposition', 'Önerme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prosecute', 'Kovuşturmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prosecution', 'Kovuşturma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prosecutor', 'Savcı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prospective', 'Muhtemel', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Prosperity', 'Refah', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Protective', 'Koruyucu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Protocol', 'Protokol', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Province', 'Vilayet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Provincial', 'İl', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Provision', 'Karşılık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Provoke', 'Kışkırtmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Psychiatric', 'Psikiyatrik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pulse', 'Darbe', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Pump', 'Pompa', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Punch', 'Yumruk', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Query', 'Sorgu', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Quest', 'Arayış', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Quota', 'Kota', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Radar', 'Radar', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Radical', 'Radikal', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rage', 'Öfkelenmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Raid', 'Yağma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rally', 'Ralli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ranking', 'Sıralama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rape', 'Tecavüz etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ratio', 'Oran', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rational', 'Akılcı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ray', 'Işın', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Readily', 'Kolayca', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Realization', 'Gerçekleştirme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Realm', 'Alem', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rear', 'Arka', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reasoning', 'Akıl yürütme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reassure', 'Güvence vermek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rebel', 'İsyancı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rebellion', 'İsyan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Recipient', 'Alıcı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reconstruction', 'Yeniden yapılanma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Recount', 'Yeniden saymak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Recruitment', 'İşe alım', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Referendum', 'Referandum', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reflection', 'Refleks', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reform', 'Reform', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Refuge', 'Sığınmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Refusal', 'Reddetme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Regain', 'Geri kazanmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Regardless', 'Ne olursa olsun', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Regime', 'Rejim', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Regulator', 'Regülatör', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Regulatory', 'Düzenleyici', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rehabilitation', 'Rehabilitasyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reign', 'Saltanat', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rejection', 'Reddetme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Relevance', 'Alaka düzeyi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reliability', 'Güvenilirlik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reluctant', 'İsteksiz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Remainder', 'Geri kalan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Remains', 'Kalıntılar', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Remedy', 'Çare', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reminder', 'Hatırlatma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Removal', 'Kaldırma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Render', 'İşlemek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Renew', 'Yenilemek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Renowned', 'Ünlü', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rental', 'Kira', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Replacement', 'Yenisiyle değiştirme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reportedly', 'Bildirilmiş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Representation', 'Temsil', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reproduce', 'Üretmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reproduction', 'Üreme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Republic', 'Cumhuriyet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Resemble', 'Benzemek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reside', 'İkamet etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Residence', 'İkamet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Residential', 'Yerleşim', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Residue', 'Kalıntı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Resignation', 'İstifa', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Resistance', 'Rezistans', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Respective', 'Saygılı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Respectively', 'Sırasıyla', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Restoration', 'Restorasyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Restraint', 'Kısıtlama', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Resume', 'Sürdürmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Retreat', 'Geri çekilme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Retrieve', 'Geri almak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Revelation', 'Vahiy', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Revenge', 'İntikam', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Reverse', 'Tersi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Revival', 'Canlanma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Revive', 'Canlandırmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Revolutionary', 'Devrimci', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rhetoric', 'Retorik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rifle', 'Tüfek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Riot', 'İsyan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rip', 'Huzur içinde yatsın', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ritual', 'Ritüel', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Robust', 'Sağlam', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rod', 'Çubuk', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rookie', 'Çaylak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Roster', 'Kadro', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rotate', 'Döndürmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rotation', 'Dönme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ruling', 'Karar', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Rumour', 'Söylenti', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sacred', 'Kutsal', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sacrifice', 'Kurban etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Saint', 'Aziz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sake', 'Hatır', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sanction', 'Yaptırım', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Scattered', 'Dağınık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Scope', 'Kapsam', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Screw', 'Vida', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Scrutiny', 'İnceleme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Seal', 'Fok', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Secular', 'Laik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Seemingly', 'Görünüşte', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Segment', 'Segment', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Seize', 'Ele geçirmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Seldom', 'Nadiren', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Selective', 'Seçici', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sensation', 'His', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sensitivity', 'Duyarlılık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sentiment', 'Duygusallık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Separation', 'Ayrılma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Serial', 'Seri', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Settlement', 'Yerleşim', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Setup', 'Kurmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sexuality', 'Cinsellik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shareholder', 'Hissedar', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shatter', 'Parçalamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shed', 'Baraka', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sheer', 'Sırf', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shipping', 'Nakliye', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shrink', 'Çekmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Shrug', 'Omuz silkme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sigh', 'İç çekmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Simulate', 'Benzetmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Simulation', 'Simülasyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Simultaneously', 'Eşzamanlı olarak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sin', 'Günah', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Situated', 'Yerleşmiş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Skeptical', 'Şüpheci', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sketch', 'Eskiz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Skip', 'Atlamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Slam', 'Çarpmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Slap', 'Tokat', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Slash', 'Küstahlık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Slavery', 'Kölelik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Slot', 'Yuva', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Smash', 'Paramparça etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Snap', 'Patlatmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Soak', 'Emmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Soar', 'Yükselmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Socialist', 'Sosyalist', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sole', 'Ayak tabanı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Solely', 'Sadece', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Solidarity', 'Dayanışma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Solo', 'Solo', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sophomore', 'İkinci sınıf öğrencisi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sovereignty', 'Egemenlik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spam', 'İstenmeyen e-posta', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Span', 'Aralık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spark', 'Kıvılcım', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Specialized', 'Özel', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Specification', 'Spesifikasyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Specimen', 'Örnek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spectacle', 'Gösteri', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spectrum', 'Spektrum', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sphere', 'Küre', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spin', 'Döndürmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spine', 'Omurga', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spotlight', 'Spot ışığı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spouse', 'Eş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Spy', 'Casus', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Squad', 'Kadro', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Squeeze', 'Sıkmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stab', 'Bıçaklamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stability', 'İstikrar', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stabilize', 'Dengelemek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stake', 'Kazıklamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Standing', 'Ayakta', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stark', 'Küstah', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Statistical', 'İstatistiksel', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Steer', 'Yönlendirmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stem', 'Kök', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stereotype', 'Stereotip', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stimulus', 'Uyarıcı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stir', 'Karıştırmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Storage', 'Depolamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Straightforward', 'Basit', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Strain', 'Gerilmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Strand', 'İplik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Strategic', 'Stratejik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Strip (narrow piece)', 'Şerit (dar parça)', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Strive', 'Çaba sarf etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Structural', 'Yapısal', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stumble', 'Yanılmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Stun v.', 'Sersemletici v.', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Submission', 'Boyun eğme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Subscriber', 'Abone', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Subscription', 'Abonelik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Subsidy', 'Sübvansiyon', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Substantial', 'Varlıklı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Substantially', 'Esas olarak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Substitute', 'Yerine geçmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Substitution', 'İkame', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Subtle', 'Hemen göze çarpmayan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Suburban', 'Banliyö', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Succession', 'Miras', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Successive', 'Birbirini izleyen', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Successor', 'Varis', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Suck', 'Emmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sue', 'Dava açmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Suicide', 'İntihar', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Suite', 'Süit', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Summit', 'Zirve', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Superb', 'Müthiş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Superintendent', 'Amir', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Superior', 'Üst', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Supervise', 'Denetlemek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Supervision', 'Nezaret', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Supervisor', 'Amir', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Supplement', 'Tamamlayıcı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Supportive', 'Destekleyici', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Supposedly', 'Sözde', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Suppress', 'Bastırmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Supreme', 'Yüce', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Surge', 'Kabarmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Surgical', 'Cerrahi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Surplus', 'Fazla', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Surrender', 'Teslim olmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Surveillance', 'Gözetim', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Suspension', 'Askıya alma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Suspicion', 'Şüphe', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Suspicious', 'Şüpheli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sustain', 'Güç vermek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Swing', 'Sallanmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Sword', 'Kılıç', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Symbolic', 'Sembolik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Syndrome', 'Sendrom', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Synthesis', 'Sentez', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Systematic', 'Sistematik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tackle', 'Olta takımı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tactic', 'Taktik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tactical', 'Taktik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Taxpayer', 'Vergi mükellefi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tempt', 'Baştan çıkarmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tenant', 'Kiracı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tender', 'Sunmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tenure', 'Görev süresi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Terminate', 'Sonlandırmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Terrain', 'Arazi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Terrific', 'Müthiş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Testify', 'Tanıklık etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Testimony', 'Tanıklık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Texture', 'Doku', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Thankfully', 'Neyse ki', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Theatrical', 'Tiyatro', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Theology', 'Teoloji', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Theoretical', 'Teorik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Thereafter', 'Daha sonra', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Thereby', 'Böylece', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Thoughtful', 'Düşünceli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Thread', 'İplik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Threshold', 'Eşik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Thrilled', 'Heyecanlı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Thrive', 'Gelişmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tide', 'Gelgit', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tighten', 'Sıkmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Timber', 'Ahşap', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Timely', 'Zamanında', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tobacco', 'Tütün', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tolerance', 'Tolerans', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tolerate', 'Tolere etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Toll', 'Geçiş ücreti', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Torture', 'İşkence', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Toss', 'Atmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Toxic', 'Zehirli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Trademark', 'Ticari marka', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Trail', 'İz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Trailer', 'Römork', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Transaction', 'İşlem', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Transcript', 'Deşifre metni', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Transformation', 'Dönüşüm', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Transit', 'Transit', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Transmission', 'Bulaşma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Transparency', 'Şeffaflık', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Transparent', 'Şeffaf', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Trauma', 'Travma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Treaty', 'Antlaşma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tremendous', 'Muazzam', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tribal', 'Kabile', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tribute', 'Haraç', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Trio', 'Üçlü', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Triumph', 'Zafer', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Trophy', 'Kupa', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Troubled', 'Sıkıntılı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Trustee', 'Mütevelli heyeti', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tuition', 'Öğrenim', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Tumor', 'Tümör', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Turnout', 'Boşaltmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Turnover', 'Ciro', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Twist', 'Bükmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Unconstitutional', 'Anayasaya aykırı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Undergraduate', 'Lisans', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Underlying', 'Altta yatan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Undermine', 'Zayıflatmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Undoubtedly', 'Kuşkusuz', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Unify', 'Birleştirmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Unprecedented', 'Benzeri görülmemiş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Unveil', 'Ortaya çıkarmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Upcoming', 'Yaklaşan', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Upgrade', 'Güncelleme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Uphold', 'Korumak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Utility', 'Faydalı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Utilize', 'Faydalanmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Utterly', 'Tamamen', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Vacuum', 'Vakum', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Vague', 'Başıboş dolaşmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Validity', 'Geçerlilik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Vanish', 'Yok olmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Variable', 'Değişken', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Varied', 'Çeşitli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Vein', 'Damar', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Venture', 'Girişim', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Verbal', 'Sözlü', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Verdict', 'Karar', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Verify', 'Doğrulamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Verse', 'Ayet', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Versus', 'Karşı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Vessel', 'Gemi', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Veteran', 'Kıdemli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Viable', 'Uygun', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Vibrant', 'Canlı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Vice', 'Mengene', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Vicious', 'Kısır', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Violate', 'İhlal etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Violation', 'İhlal etme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Virtue', 'Erdem', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Vocal', 'Vokal', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Vow', 'Yemin etmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Vulnerability', 'Güvenlik açığı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Vulnerable', 'Hassas', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Ward', 'Koğuş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Warehouse', 'Depo', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Warfare', 'Savaş', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Warrant', 'Garanti', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Warrior', 'Savaşçı', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Weaken', 'Zayıflatmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Weave', 'Örgü', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Weed', 'Esrar', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Well-being', 'Refah', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Whatsoever', 'Herhangi bir', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Whereby', 'Ki', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Whip', 'Kırbaçlamak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Wholly', 'Tamamen', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Widen', 'Genişletmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Widow', 'Dul', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Width', 'Genişlik', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Willingness', 'İstekli olma', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Wipe', 'Silip süpürmek', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Wit', 'Zekâ', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Withdrawal', 'Para çekme', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Workout', 'Antrenman yapmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Worship', 'Tapmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Worthwhile', 'Değerli', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Worthy', 'Saygıdeğer', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Yell', 'Bağırmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Yield', 'Teslim olmak', 'C1', NULL, 0, 0);
INSERT INTO words
VALUES ('Aberration', 'Sapma', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Absolve', 'Anlatmak', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Acrimonious', 'Acımasız', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Admonish', 'Uyarmak', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Alacrity', 'İsteklilik, canlılık', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Allude', 'İma etmek', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Altruistic', 'Fedakar', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ameliorate', 'Düzeltmek', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Animosity', 'Düşmanlık', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Apathetic', 'İlgisiz', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Apprehensive', 'Endişeli', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Arduous', 'Zorlu', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Assiduous', 'Gayretli', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Astute', 'Zekice', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Audacious', 'Cüretkâr', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Austere', 'Sade', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Benevolent', 'Yardımsever', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bolster', 'Desteklemek', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Bombastic', 'Bombardıman', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Brevity', 'Kısalık', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cacophony', 'Kakofoni', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cajole', 'Tatlı dille kandırmak, ikna etmek', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Capricious', 'Kaprisli', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Censure', 'Kınama', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Chicanery', 'Şık', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Circumspect', 'İhtiyatlı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Coalesce', 'Bir araya gelmek', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Cogent', 'İkna edici, inandırıcı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Colloquial', 'Günlük konuşma dili', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Conciliatory', 'Uzlaştırıcı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Confluence', 'Kavşak, birleşme noktası', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Conjecture', 'Varsayım', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Convoluted', 'Kıvrımlı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Copious', 'Bol', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Debacle', 'Yıkım', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Debilitate', 'Zayıflatmak', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Deleterious', 'Zararlı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Deride', 'Alay etmek, küçümsemek', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Despondent', 'Umutsuz', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dichotomy', 'İkilik', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Didactic', 'Didaktik', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Diffident', 'Çekingen, kendine güveni olmayan', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Disparate', 'Farklı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Dogmatic', 'Dogmatik', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ebullient', 'Coşkulu, taşkın', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Egregious', 'Acımasız', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Elicit', 'Meydana çıkarmak', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Elucidate', 'Açıklığa kavuşturmak', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ephemeral', 'Geçici', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Erudite', 'Bilgili', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Esoteric', 'Ezoterik', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Exacerbate', 'Şiddetlendirmek', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Exonerate', 'Temize çıkarmak', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Extraneous', 'Yabancı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Facetious', 'Yüzlü', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fastidious', 'Titiz', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Fervent', 'Ateşli', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Garrulous', 'Garip', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Grandiloquent', 'Gösterişli konuşan, tumturaklı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hackneyed', 'Basmakalıp, klişe', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Hapless', 'Şanssız', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Immutable', 'Değişmez', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Impeccable', 'Kusursuz', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Incongruous', 'Uyuşmaz', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Insatiable', 'Doyumsuz', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Magnanimous', 'Bağışlayıcı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Malevolent', 'Kötü niyetli', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mendacious', 'Yalancı, gerçek olmayan', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mercurial', 'Değişken, dengesiz', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Mitigate', 'Azaltmak', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Nonchalant', 'Kayıtsız, ilgisiz', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Obfuscate', 'Şaşırtmak', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Obsequious', 'Yaltakçı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ostentatious', 'Gösterişli', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Paragon', 'Mükemmel örnek, üstün örnek', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pejorative', 'Aşağılayıcı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Perfunctory', 'Formalite icabı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pernicious', 'Zararlı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Perspicacious', 'Göze çarpan', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pertinacious', 'Özenli', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Petulant', 'Küstah', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Platitude', 'Basmakalıp söz, klişe', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Plethora', 'Bolluk', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Pragmatic', 'Pragmatik', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Precarious', 'Güvenilmez', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Precocious', 'Erken', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Proclivity', 'Eğilim', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Prosaic', 'Sıradan, sıkıcı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Quixotic', 'Hayalperest, gerçekdışı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Rancor', 'Kin, öfke', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Recalcitrant', 'İnatçı, dik başlı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Recondite', 'Anlaşılması zor, derin', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Redundant', 'Gereksiz', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Relegate', 'Boşaltmak', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sagacious', 'Bilge, akıllı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Salient', 'Göze çarpan', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sanguine', 'İyimser, umutlu', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Scrupulous', 'Titiz', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Sycophant', 'Yalaka, dalkavuk', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tangential', 'Teğet', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Tenacious', 'İnatçı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Ubiquitous', 'Her yerde bulunan', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Unctuous', 'Küstah', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Vacillate', 'Boşaltmak', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Vapid', 'Sıkıcı, tatsız', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Vehement', 'Şiddetli', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Verbose', 'Ayrıntılı', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Vicissitude', 'Değişim, iniş çıkış', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Vindicate', 'Onaylamak', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Vociferous', 'Sesli', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Winsome', 'Sevimli, cana yakın', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Zealous', 'Hevesli', 'C2', NULL, 0, 0);
INSERT INTO words
VALUES ('Abbreviate', '(1) kısaltmak, özetlemek (2) (matematikte) sadeleştirmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Abstain from', '(alkol, ilaç vb) --- den sakınmak/ uzak durmak (=avoid from) !', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Abundance', 'Bolluk, bereket', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Abundant', 'Bol, bereketli', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Accessible to', 'Ulaşılabilir, erişilebilir', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Accuse (of)', 'Birini bir şeyle suçlamak, itham etmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Acknowledge as',
        '(1) kabul etmek, --- olarak tanımak (2) (mektup, mesaj vb) aldığını gönderen kişiye bildirmek', NULL, NULL, 0,
        0);
INSERT INTO words
VALUES ('Acquainted with', 'Aşina olmak, haberdar olmak (= familiar with)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Addict', 'Bağımlı, tiryaki *** drug addict = eroin bağımlısı', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Addiction to', 'Bağımlılık, tiryakilik', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Adore', 'Çok sevmek, tapmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Aggravate', 'Gittikçe kötüye gitmek, fenalaşmak (= deteriorate, worsen)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Alien (to)', 'Yabancı', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Alongside', 'Yanında, bitişiğinde (beside, next to)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Alteration', 'Değişiklik', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Appalling', 'Korkunç (= dreadful, horrendous)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Approve of', 'Onaylamak, uygun bulmak, tasvip etmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Artefact', 'İnsan eliyle yapılmış (sanat)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Ascend', 'Yukarı çıkmak, yükselmek, tırmanmak (= go up / climb up)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Ask for', 'Ricada bulunmak, bir şey istemek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Assist somebody in something', 'Birine bir konuda yardım etmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Astonishment', 'Şaşırtmak, şaşırmak (= amazement, bewilderment)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Attainment', 'Ulaşmak, erişmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Auditorium', 'Dinlenme/izleme salonu, seyircilerin oturduğu bölüm', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Avert', '(1) olmasını önlemek (2) başka yöne çevirmek (trafik akışını vb)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Avoidable', 'Kaçınılabilir, engellenebilir', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Backward', 'Geri kalmış, geriye doğru', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Badly in need of', 'Bir şeye/birine çok muhtaç olmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Barren', 'Kurak, verimsiz (= infertile, arid)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Bazaar', 'Pazar, alışveriş yeri', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Belongings', 'Birinin kişisel eşyaları (= possessions)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Bitingly satirical', 'Aşırı alaycı, insafsızca eleştirme', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Blaze', '(1) ateş, alev, yangın (2) parlamak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Bolt', 'Fırlayıp kaçmak, tabanları yağlamak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Break off', '(nişan, nikah vb) bozmak, ayrılmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Bribery', 'Rüşvet *** offer bribes = rüşvet teklif etmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Bring up', '(1) çocuk büyütmek (2) kusmak (3) ortaya (konu vb) atmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Broadly speaking', 'Genel konuşmak gerekirse (= generally, mostly)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Broil', 'Izgara yapmak, kavurmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Bullfight', 'Boğa güreşi', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Bully', '(1) kabadayı, zorba (2) kabadayılık yapmak, zorbalık yapmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Calculator', 'Hesap makinesi', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Call for', 'Talep etmek, istemek (= demand)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Can’t take one’s eyes off', 'Gözlerini birinden veya bir şeyden alamamak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Captivating', 'Büyüleyici (= enchanting, fascinating)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Captive', 'Tutsak, esir', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Captivity', 'Tutsaklık, esaret', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Carry out', '(çalışma, deney, anket vb) yürütmek, icra etmek (= fulfil, conduct)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Casually', 'Günlük, sıradan, havadan sudan', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Ceaseless', 'Aralıksız, durmadan (= non-stop)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Census', 'Nüfus sayımı', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Charge (with)', '--- ile yargılamak (mahkemede) (= try)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Claw', 'Pençe, hayvan pençesindeki kıvrık tırnak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Clearance', '(1) mağazayı boşaltma, malları elden çıkarma, tasfiye (2) izin, yeşil ışık', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Coincide with', 'Aynı zamana denk gelmek/tesadüf etmek (= fall on the same date)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Collapsible', 'Katlanabilir (kanepe vb)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Collar', '(1) yaka (2) tasma', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Collide with', 'Çarpışmak (= crash into)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Comment on', 'Yorum yapmak (= interpret)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Commonplace', 'Yaygın, sıradan (= ordinary, usual)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Commuter', 'Ev ile iş arasında mekik dokuyan/gidip gelen', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Compensation for', '(1) tazminat ödemek (2) telafi etmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Compete against', 'Başkasıyla yarışmak, rekabet etmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Compete with',
        'Başkasıyla aynı yerden beslenmek/geçim sağlamak (kangurular koyunların otlaklarından otlanan rakip hayvanlar olması gibi)',
        NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Complain to somebody about something', 'Şikayet etmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Comply (with)', '--- e uymak,--- e itaat etmek (= abide by)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Conceive as',
        '(1) --- olarak algılamak/düşünmek (2) conceive of = bir şeyi ilk kendisi akıl etmek (= senaryonun konusu vb) (3) gebe kalmak',
        NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Conditionally', 'Şartlı olarak, belli şartlara bağlı', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Confident (of)', 'Emin', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Confine to', '(1) sınırlamak, bir yere mahkum etmek (2) hapse atmak (= imprison)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Conflict with', 'Çatışmak, çarpışmak, savaşmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Conform to', 'Uymak, uyuşmak (= obey the rules)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Consent to', 'Razı olmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Considerately', 'Düşünceli/nazik bir şekilde', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Consist of', 'İbaret olmak, meydana gelmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Conspire against', 'Birine komplo kurmak (= plot against)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Constrain', 'Zorlamak (= restrain, force)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Contact with', 'Birisi ile kontak/temas kurmak, irtibata geçmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Content with', '--- den memnun', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Contradict', 'Çelişmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Contradictory', 'Çelişkili, tutarsız, kendini yalancı çıkaran (= inconsistent)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Contribute to', 'Katkıda bulunmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Converse', '(1) karşıt, zıt (2) konuşmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Convert into', 'Dönüştürmek (= change)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Correspond to', 'Bir şeyle uymak, uygun düşmek, tekabül etmek (= agree, match)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Correspond with', 'Birisi ile yazışmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Cramped', 'Hijyenik olmayan', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Credibly', 'İnanılır bir şekilde (= believably)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Crippled', 'Felçli, kötürüm (= paralysed) (2) engellenmiş, gerilemiş (ekonomi vb)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Crocodile', 'Timsah (= alligator)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Cross out', 'Üstünü çizmek, silmek (= delete)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Crumble', 'Ufalanmak, parçalanmak (= disintegrate, fall apart)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Curator', 'Sanat galerisi/müze/kütüphane görevlisi', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Customary', 'Geleneksel (= traditional', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Deceit', 'Kandırmak *** deceitful = hilekar, hileci', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Deceive', 'Kandırmak, kafaya almak (= take in)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Decipher', 'Şifresini çözmek, anlamını meydana çıkarmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Dedicate', 'Kendini adamak (= devote to, commit oneself to)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Dedicate to', 'Kendini adamak (= devote to)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Deduce', 'Sonuç çıkarmak (= conclude, assume)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Deduction', 'Tümevarım, sonuç (= conclusion)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Deepen', 'Derinleştirmek, derinleşmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Degeneration', 'Yozlaşma, aslını kaybetme', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Delightful', 'Zevkli, hoş', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Demobilize', 'Askerden terhis etmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Demolish', 'Yıkmak, parçalamak (= do away with)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Depress', '(1) üzmek (= sadden, upset) (2) bastırmak (= press down)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Derive from', 'Çıkarmak, gelmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Desolate', 'Mutsuz, kederli (= depressed) (2) terkedilmiş (= deserted)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Dessert', 'Tatlı', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Destiny', 'Kader, kısmet', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Deter (someone) from', 'Caydırmak, engel olmak (= discourage)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Deviate', 'Sapmak, yönünü değiştirmek (= diverge, stray)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Diagnose as', 'Teşhis etmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Disappearance', 'Ortadan/gözden kaybolmak (= vanish)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Discriminate (against)', '(ırk, yaş, cinsiyet vb) ayrımcılık yapmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Discriminate against', 'Ayrımcılık yapmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Discuss about', 'Tartışmak (= argue)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Dispatch', 'Göndermek, yollamak (= send, submit)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Displeased', 'Hoşnut kalmamış, memnun olmayan (= discontented, unsatisfied)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Dispose of', 'Başından atmak, --- den kurtulmak (= get rid of)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Disqualify', 'Diskalifiye etmek, elemek, yetersiz görmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Disseminate', '(bilgi, fakir vb) yaymak, dağıtmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Dizzy', 'Başı dönen, kendini bayılacak gibi hisseden (= giddy)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Docile (dosayl)', 'Uysal, evcil', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Dowry', 'Çeyiz', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Dress code', '(bir işyerinde veya okulda) kıyafet genelgesi', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Drug addict', 'Eroin bağımlısı', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Drug dealer', 'Eroin ticareti yapan kişi', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Dustbin', 'Çöp kutusu (= trash can)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Earth*****', 'Deprem', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Elimination', '(1) ortadan kaldırma, yok etme, bertaraf etme (2) hesaba katmama', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Embarrass', 'Utandırma (= humiliate)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Emulate', 'Taklit etmek,(= imitate, copy)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Enclose', 'Çevresini sarmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Enhancement', 'Yükseltme, artırma, çoğaltma (= improvement, enrichment)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Enlarge', 'Büyütmek, genişletmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Enquire', 'Soruşturmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Enslave', 'Köleleştirmek, esir etmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Envy', 'Kıskanmak, imrenmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Epic', 'Destan, destansı (şiir vb)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Equate', 'Eşitlemek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Erode', 'Yıpratmak, aşınmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Evidently', 'Açık ve şüphe götürmez bir şekilde, delillere dayanarak (= obviously)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Exaggerated', 'Abartılı, mübalağalı', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Excavate', 'Kazı yapmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Exclusive to', 'Herkese açık olmayan, özel (otel, tatil yeri vb)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Excursion', 'Keşif gezisi', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Exhibit', 'Sergilemek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Expiry date', 'Son kullanma tarihi', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Fabricate', '(1) uydurmak (= make up) (2) (raf vb) monte etmek (= put up)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Faint', '(1) bayılmak (= pass out) (2) solgun (ses, renk vb)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Falsify', '(1) hesaplar üzerinde oynamak (2) sahtekarlık yapmak (= fake)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Familiar (with)', 'Aşina, tanıdık', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Famish', 'Aç kalmak, açlıktan ölmek (= starve)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Favourable', 'Olumlu, yapıcı (= positive, constructive) (2) uğurlu (=auspicious)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Fearful for', '--- için korkan/endişelenen', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Fertilize', '(toprağı vb) verimli hale getirmek, verimli kılmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Fetch', 'Gidip getirmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Fiancé', '(erkek) nişanlı', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Fiancée', '(kız) nişanlı', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Field trip', 'Kır gezisi, arazi gezisi', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Filthy', '(1) pis, kirli (2) dayanıksız, sağlam olmayan', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Flatmate', 'Ev arkadaşı', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Flattery', 'Birine yağ çekme', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Fleece', 'Koyun postu (yünlü) *** hide = yünsüz post', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Fluctuate', 'Dalgalanmak, istikrarlı gitmemek, bir artmak bir azalmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Focus on', 'Odaklanmak, yoğunlaşmak (= concentrate on, centre on)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Forceful', '(1) güçlü, zorlu (2) etkili, ikna edici', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Forgery', 'Sahtekarlık (= counterfeit, fake), kalpazanlık', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Fortify', 'Takviye etmek, sağlamlaştırmak, kuvvetlendirmek (= strengthen, enrich)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Fracture', 'Kırılmak, çatlamak ( kemik, kolon vb)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Frail', 'Zayıf, cılız (= feeble)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Freed', 'Serbest kalmış, özgür (= at liberty, at large)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Fulfil', '(görev, sorumluluk vb) yerine getirmek, icra etmek (= carry out)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Fussy', 'Aşırı titiz (= fastidious, meticulous, diligent)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Fuzzy', 'Tüylü', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Genre (', 'Canr) = tür, çeşit, nevi (= type, sort)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Get rid of', 'Başından atmak, defetmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Give up', 'Vazgeçmek, bırakmak (= abandon, abort)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Gloom', 'Karanlık ***gloomy = üzüntülü, hüzünlü', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Glorify', 'Yüceltmek, övmek (= praise)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Graveyard', 'Mezarlık (= cemetery)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Groom', 'Damat', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Grow tired of', '--- den yorulmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Growl', 'Köpek ve benzeri hayvanların çıkardığı hırlama sesi', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Harass', 'Saldırmak, taciz etmek ******ual harassment = cinsel taciz', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Harbour', '(1) liman (2) barındırmak, sağlamak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Hardship', 'Zorluk', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Harshly', '(1) sert bir şekilde (2) kabaca', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Hasten', 'Acele etmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Havoc', 'Hasar, yıkım (= destruction)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Hazardous', 'Tehlikeli (= perilous)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Hectic', 'Heyecanlı, telaşlı, hareketli (program, ofis vb)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Hinder', '(1) engel, mani (2) engel olmak, mani olmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Hitchhiker', 'Otostopçu', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Humiliate', 'Aşağılamak, rezil etmek, utandırmak (= embarrass)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Hunter', 'Avcı', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Hurricane (hörikeyn)', 'Kasırga', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Iceberg', 'Buz dağı (= glacier)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Idle', 'Tembel (= lazy, indolent) x (= hardworking)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Imitate', 'Taklit etmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Immobilize (immmobilayz)', 'Hareketsiz/sabit kılmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Impeach', 'Suçlamak, itham etmek (= accuse)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Implicate', 'Bulaştırmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Imprisonment', 'Hapse atmak (= incarceration)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Inaudible', 'Duyulamaz, işitilemez (ses vb)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Incapable of (inkepibil)', 'Kabiliyetsiz, yeteneksiz (= unskillful)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Incapacitate', 'Yetersiz bırakmak, olanak tanımamak, aciz bırakmak (= debilitate)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Incessant', 'Aralıksız, sürekli', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Incline', 'Eğmek, eğilimi olmak, fikrini vermek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Incorporate into', 'Dahil etmek (= include, integrate)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Indifference to', 'Kayıtsız, ilgisiz olmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Influential (influwenşil)', 'Nüfuzlu, sözü geçer, çevresi geniş (=well-connected)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Initiate (inişiyeyt)', 'Başlatmak (= start, commence)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Innovate', 'Yeni bir şey icat etmek, yenilik getirmek (= invent)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Insatiable (inseyşibil)', '(1) gözü doymaz, doyumsuz, aç gözlü (2) obur, pisboğaz', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Insignificant', '(1) ehemmiyetsiz, önemsiz (2) anlamsız, manasız', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Insist (on)', 'Israr etmek (= persist in)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Instantaneously', 'Anlık, bir anda olan, aniden (= immediately, instantly)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Insulate (against)', 'Yalıtmak, (soğuğu/sesi vb) kesmek (hırkanın soğuğu kesmesi gibi)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Intention (intenşin)', 'Niyet', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Intentional', 'Kasıtlı,maksatlı,bilebile (= deliberately)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Interaction (with)', 'Etkileşim', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Interfere with', 'Karışmak, müdahale etmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Interrogate', 'Sorguya çekmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Invaluable', 'Paha biçilmez, çok değerli (= priceless)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Invest (in)', 'Para yatırımı yapmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Irregularity', '(1) yolsuzluk, hile (2) düzensizlik', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Jeopardize (ciopidayz)', 'Tehlikeye atmak (= endanger, imperil)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Kennel', 'Köpek kulübesi', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Keyhole', 'Anahtar deliği', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Kidnapper', 'Adam/çocuk kaçıran (= abductor)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Lamb', '(1) kuzu (2) kuzu eti', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Legislate', 'Yasamak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Lessen', 'Azaltmak (= diminish)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Levy', 'Zorla toplama (haraç)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Listless', 'Yorgun, bitkin (= exhausted)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Loathe', 'Nefret etmek (= abhor, hate)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Luggage (lagiç)', 'Bagaj', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Magical (mecikil)', 'Sihirli', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Make a decision', 'Karar vermek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Massacre (messeki', 'R) = soykırım, katliam (= genocide)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Masterpiece', 'Şaheser, baş yapıt', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Mature (maçu', ') = olgun', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Meadow', 'Çayır, otlak, mera (= pasture)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Meander', '(1) dolambaçlı yol (2) avare avare dolaşmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Measure (meji', 'R) = (1) ölçü, ölçmek (2) tedbir, önlem (= precaution)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Mediate between', 'Arabuluculuk etmek, arasını bulmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Migrate', 'Göçmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Miraculously', 'Mucize eseri', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Misbehave', 'Terbiyesizlik yapmak, kötü davranışlar sergilemek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Mischief', 'Yaramazlık, haşarılık (= misbehaviour)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Misunderstanding', 'Yanlış anlaşılma (= misconception)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Mix up', 'Aklını karıştırmak,karıştırmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Mock at', 'Dalga geçmek, alay etmek (= tease, make fun of)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Mourning', 'Yas, keder (= lamentation) ***mournful = yaslı, yas tutan', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Multinational', 'Çok uluslu', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Municipality', 'Belediye', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Narrowly', 'Kıl payı (= she narrowly escaped death yesterday.)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Native to', 'Yöreye has/özgü', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Neutrality (nötraliti)', 'Tarafsızlık (= impartiality)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Obese', 'Şişman, obez', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Obscure', '(1) silik (2) anlaşılmaz hale getirmek, karışık hale getirmek (= confuse)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Obsolete', 'Modası geçmiş, eskide kalmış', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Oddly enough!', 'Ne tuhaftır ki …!', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Odour', 'Koku ***odourless = kokusuz x (aromatic = hoş kokulu)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Officially', 'Resmen, resmi olarak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Oppress', 'Zulmetmek (= persecute)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Ornament', '(1) süs, süs eşyası (2) süslemek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Orphan', 'Yetim bırakmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Outcrop', 'Yeryüzüne çıkmış katman', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Outcry', 'Feryat figan, çığlık', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Outdo', 'Birini geride bırakmak, sollamak, ekarte etmek (= surpass)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Overlap', 'Üstüste binmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Overtake', '(arabasıyla bir başka arabayı) sollamak, bastırmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Participate in', 'Katılmak, iştirak etmek (= take part in, join, attend)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Particular (pitik', 'Ulır) = özel, önemli *** in particular = özellikle', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Passionately', 'İhtirasla, tutkuyla', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Patiently', 'Sabırla, sabırlı bir şekilde (= uncomplainingly)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Pavement', 'Kaldırım (= side-walk)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Peace and quiet', 'Huzur ve sükunet', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Pedestrian', 'Yaya', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Penalize', 'Ceza vermek, cezalandırmak (= punish)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Persevering', 'Sebatkar, gayretli', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Pessimism', 'Kötümserlik ***pessimist = kötümser ***optimist = iyimser', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Plague (pleyg)', '(1) veba (2) öldürücü salgın hastalık (3) (bela vb) musallat olmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Pledge (plec)', 'Ciddi bir söz vermek, ciddi bir vaat', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Polio', 'Çocuk felci', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Practically', '1-hemen hemen 2-uygun olarak, pratik olarak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Prior (to)', '--- den önce, --- den evvel', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Promptly', 'Derhal, hemen', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Proofread', 'Bir metni inceleyip üzerindeki yanlışları düzeltmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Protection against', 'Koruma', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Purchase (pö', 'Çıs) = (1) satın almak (2) satın alınan eşya', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Put forth', 'Öne sürmek, ortaya atmak (= put forward, bring up)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Race', '(1) ırk (2) yarış', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Reckless', '= dikkatsiz, pervasız (= irresponsible, thoughtless)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Recklessly', 'Dikkatsizce, pervasızca (= irresponsibly, thoughtlessly)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Recognize', '(daha önce gördüğü birini veya bir şeyi gördüğünde) tanımak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Refreshing', 'Canlandırıcı, serinletici (aperatif yiyecek, temiz hava vb)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Refund', 'Parayı iade etmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Regretful', 'Pişman, üzgün (= remorseful)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Regrettable', 'Üzücü, üzüntü/keder/esef verici', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Rehearse (rihörs)', 'Prova yapmak ***rehearsal = prova', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Relentless', '(1) merhametsiz (2) amansız, hummalı, aralıksız devam eden', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Relocate', 'Yerini değiştirmek, yerinden etmek (= displace)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Reluctant (rilaktint)', 'İsteksiz (= unwilling)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Remembrance', 'Anma, hatırlama, yad etme (= commemoration)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Reminiscent of', 'Andıran, hatırlatan, anımsatan (= suggestive of)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Repeatedly', 'Defalarca, tekrar tekrar (= continually, constantly)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Repetitive', 'Monoton, sıkıcı', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Replace (with)', '(1) eski yerine koymak (2) --- ile değiştirmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Replica', 'Aslına çok benzeyen kopya', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Resentful', 'Alıngan, darılmış', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Resign from', '--- den istifa etmek ***resignation = istifa', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Restlessness', 'Huzursuzluk, içinin rahat olmaması x calmness', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Revolve', '(1) dönmek (2) döndürmek, çevirmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Rightfully', 'Haklı olarak, haklı yere x wantonly = durduk yere, sebepsiz yere', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Rob somebody of something', 'Birini soymak ***robbery = soygun', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Robust (ribast)', 'Turp gibi, sapasağlam', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Rough (raf)',
        '(1) kaba pürüzlü (zemin, yüzey vb) (2) nazik olmayan, sakar bir şekilde (3) (deniz/okyanus için) dalgalı, fırtınalı',
        NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Rubble', 'Enkaz, yığın (= wreckage)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Salute', 'Selamlamak (= greet)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Savage', 'Vahşi', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Scald', 'Kaynar suyla yakmak/haşlamak (el, kol vb)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Scalp', 'Kafa derisini yüzmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Scarce', 'Seyrek, az', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Scarcely', 'Hemen hemen hiç (= barely, hardly)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Scatter', 'Saçmak, serpmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Sceptical', 'Şüpheci (= cynical)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Seam', '(1) kıyafetlerin dikiş yerleri (2) (yara için) dikiş yeri', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Seasonal', 'Mevsimine uygun', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Sedate', '(1) sakinleştirmek, yatıştırmak (2) sakin, soğukkanlı (= composed)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Sentimental', 'Duygusal (= emotional)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Sewage', 'Lağım, kanalizasyon', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Shortcoming', 'Kusur, eksik, noksan', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Shorten', 'Kısaltmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Show off', 'Hava atmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Shuffle', 'Karıştırmak ( iskambil kağıtlarını); ayak sürüyerek yürüme', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Situate', 'Konuşlandırmak, yerleşmek, yerleştirmek (= locate)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Slaughter', '(1) kurban etmek, kesmek (2) öldürmek, cinayet işlemek (= murder)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Sleeve', 'Gömlek, gömlek kolu *** buy on the sleeve = veresiye satın almak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Smother (smadir)', '(1) (yastık vb ile) boğmak (2) üzerini örtmek, kamufle etmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Sociable', 'Sıcak kanlı, insanlarla çabuk kaynaşan', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Soothing', 'Yatıştırıcı (= comforting, calming)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Spillage', '(yere vb) dökülen şey, döküntü (su vb)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Spoiled', 'Şımarık (= mischievous (=misçivıs)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Startle', '(1) korkutmak, ürkütmek (2) şaşırtmak, affalatmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Statue (steyçu)', 'Heykel', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Stealthily (steltili)', 'Hırsız gibi, sinsi bir şekilde (= sneakily (snikili)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Stray', '(1) başıboş aylak kimse (2) sokakta yaşayan kedi, köpek vb', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Stroll', 'Ağır ağır dolaşmak (= go for a stroll = dolaşmaya çıkmak)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Subject to', '(1) (ölüme, yalnız kalmaya vb) maruz kalmış (2) olası, muhtemel', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Suffrage', 'Oy kullanma hakkı', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Suggestive of', 'Manalı, imalı, insanın aklına bir şey getiren', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Surpass', 'Üstün olmak, geride bırakmak, üstün olmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Take off', '(1) havalanmak (2) taklit emek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Take on', '(sorumluluk vb) üstlenmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Tame', 'Evcil hayvan (= docile, domesticated)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Tapestry', 'Duvar halısı', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Tasteful', '(1) zevkli, zevkine düşkün kişi (2) zevkle yapılan/hazırlanan (desen', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Tasty', 'Lezzetli', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Temple (tempil)', 'Tapınak, mabet (= shrine, sanctuary)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Tenderness', 'Şefkat, merhamet, anlayış (= affection)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('The rest of…', '--- nın geri kalanı', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Thoughtless', 'Düşüncesiz, patavatsız, kaba (= tactless, rude)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Throw out', '(çöp vb) dışarı atmak', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Thunderstorm', 'Yıldırımlı fırtına', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Tomb', 'Mezar, kabir, türbe (= grave)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Trash', 'Çöp (= garbage)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Treasure (treji', ')= hazine', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Trim', '(1) (ağaç) budamak (2) (saç) kırpmak, kesmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Tripe', 'İşkembe', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Truthful about', '(1) sadece doğruyu söyleyen (2) gerçeklere uygun, doğru (söz)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Turn in', '(1) (yetkili kişiye) teslim etmek (2) uyumaya gitmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Unattended', 'Sahipsiz, sahibi ortada gözükmeyen (eşya, çocuk vb)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Unbearable', 'Katlanılmaz, dayanılmaz (baskı, sıcaklık, soğuk vb) (=intolerable)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Uncultured', 'Kültürsüz, tahsilsiz, cahil (= uncultivated, boorish, unsophisticated)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Unlimited', 'Sınırsız (= unrestricted)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Unreliable', 'Güvenilmez', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Untimely', 'Vakitsiz, yersiz, olmadık zamanda (= at an awkward time)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Unwind', '(1) (özellikle işten sonra) rahatlamak, dinlenmek (2) düğüm/sargıçözmek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Vague (veyg)', '(1) belirsiz, üstü kapalı (2) net hatırlanamayan şey x vivid', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Vet', 'Veteriner', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Vigorously', 'Gayretle (= diligently)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Violate (vayoleyt)', '(kural, kanun, hak vb) ihlal etmek, çiğnemek (= abuse)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Virtually', 'Hemen hemen, neredeyse (= practically, nearly, almost)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Vocation', 'Meslek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Vote for/against', '(1) oy (2) oy vermek', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Voyage', 'Deniz yolculuğu', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Wantonly', '(1) durduk yere, sebepsiz yere (2) ahlaksızca, şehvetle', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Weep', 'Ağlamak, sızlamak (= cry, sob)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Whirl', '(1) hızla dönmek (2) girdap', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Withdraw from',
        '(1) (savaştan,seçimlerden vb) geri çekilmek (= pull out of) (2) (bankadan, hesaptan vb) para çekmek', NULL,
        NULL, 0, 0);
INSERT INTO words
VALUES ('Withdrawn', 'İçine kapanık (= reserved, inhibited)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Worthless', 'Değersiz (= valueless)', NULL, NULL, 0, 0);
INSERT INTO words
VALUES ('Zip', 'Fermuar', NULL, NULL, 0, 0);
CREATE TABLE ` statistics `
(
    `
    stat_id
    `
    INTEGER
    PRIMARY
    KEY
    AUTOINCREMENT
    NOT
    NULL,
    `
    correct_count
    `
    INTEGER
    NOT
    NULL,
    `
    wrong_count
    `
    INTEGER
    NOT
    NULL,
    `
    skipped_count
    `
    INTEGER
    NOT
    NULL,
    `
    learned
    `
    INTEGER
    NOT
    NULL
);
INSERT INTO statistics
VALUES (0, 0, 0, 0, 0);
CREATE TABLE ` exams `
(
    `
    exam
    `
    TEXT
    NOT
    NULL,
    PRIMARY
    KEY
(
    `
    exam
    `
));
INSERT INTO exams
VALUES ('TOEFL');
INSERT INTO exams
VALUES ('IELTS');
INSERT INTO exams
VALUES ('YDS');
INSERT INTO exams
VALUES ('YÖKDİL');
INSERT INTO exams
VALUES ('KPDS');
INSERT INTO exams
VALUES ('Mixed');
CREATE TABLE ` word_exam_cross_ref `
(
    `
    word
    `
    TEXT
    NOT
    NULL,
    `
    exam
    `
    TEXT
    NOT
    NULL,
    PRIMARY
    KEY
(
    `
    word
    `,
    `
    exam
    `
));
INSERT INTO word_exam_cross_ref
VALUES ('abandon', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('abbreviate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('abolish', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('absorb', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('abstain from', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('abundance', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('abundant', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('accelerate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('accept', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('access', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('accessible to', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('accommodate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('accompany', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('accomplish', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('accumulate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('accuracy', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('accurate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('accurately', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('accuse (of)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('achieve', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('acknowledge as', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('acquainted with', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('acquire', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('acquisition', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('activity', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('adapt', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('addict', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('addiction to', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('addition', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('additionally', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('adequately', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('adjust', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('adjustment', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('administer', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('admire', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('admit', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('adopt', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('adore', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('adverse', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('advocate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('affect', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('aggravate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('aggressive', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('aid', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('alien (to)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('alongside', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('alter', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('alteration', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('amazing', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('amend', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('amendment', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('amusing', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('announce', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('anticipate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('apologize', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('appalling', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('appointment', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('appreciate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('approach', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('appropriately', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('approve of', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('arrange', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('artefact', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('ascend', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('ask for', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('aspire', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('assemble', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('assess', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('assign', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('assist somebody in something', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('associate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('assume', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('assure', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('astonishment', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('attach', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('attack', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('attain', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('attainment', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('attend', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('attribute', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('auditorium', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('available', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('avert', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('avoidable', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('award', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('backward', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('badly in need of', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('barely', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('bargain', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('barren', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('basic', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('bazaar', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('behave', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('believe', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('belongings', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('beloved', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('bitingly satirical', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('bizarre', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('blanket', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('blaze', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('bolt', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('branch', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('break off', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('breed', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('bribery', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('bride', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('brief', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('bring up', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('broadcast', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('Broadly speaking', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('broil', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('bullfight', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('bully', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('burial', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('burn', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('button', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('calculator', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('call for', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('calm', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('can’t take one’s eyes off', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('cancel', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('captivating', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('captive', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('captivity', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('capture', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('careless', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('carry out', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('carve', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('casually', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('caution', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('cease', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('ceaseless', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('celebration', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('celebrity', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('census', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('ceremony', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('charge (with)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('circulate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('circulation', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('cite', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('citizen', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('clarify', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('claw', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('clearance', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('close', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('closed', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('closure', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('coincide with', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('collapse', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('collapsible', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('collar', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('colleague', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('collide with', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('commence', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('comment on', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('commercial', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('commit', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('common', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('commonplace', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('commuter', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('companion', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('company', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('compel', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('compensation for', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('compete', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('compete against', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('compete with', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('competition', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('compile', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('complain to somebody about something', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('completely', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('comply (with)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('compose', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('compound', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('comprise', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('compute', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('conceal', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('conceive as', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('conclude', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('conclusion', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('condition', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('conditionally', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('conduct', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('confess', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('confident (of)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('confine to', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('confirm', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('conflict', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('conflict with', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('conform to', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('confront', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('confuse', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('conquer', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('consent', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('consent to', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('consequence', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('conserve', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('considerable', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('considerably', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('considerately', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('consideration', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('consist of', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('conspire against', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('constantly', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('constitute', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('constrain', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('construct', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('consult', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('consume', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('contact with', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('contemporary', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('content with', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('contest', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('continent', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('contract', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('contradict', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('contradictory', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('contribute to', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('controversial', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('controversy', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('conventional', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('converse', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('convert into', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('convict', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('convince', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('correctly', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('correspond to', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('correspond with', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('counterpart', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('couple', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('course', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('cramped', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('crash', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('crawl', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('create', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('credibly', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('criminal', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('crippled', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('crocodile', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('cross out', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('crumble', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('cultivate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('curator', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('currency', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('curve', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('custom', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('customary', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('debate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('debt', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('deceit', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('deceive', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('decipher', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('declare', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('decline', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('dedicate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('dedicate to', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('deduce', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('deduction', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('deepen', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('defeat', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('defect', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('defend', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('define', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('degeneration', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('delay', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('delightful', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('deliver', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('demand', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('demobilize', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('demolish', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('demonstrate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('deny', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('depress', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('derive from', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('descend', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('desert', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('deserve', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('design', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('desire', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('desolate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('dessert', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('destination', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('destiny', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('destroy', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('detain', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('detect', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('detection', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('deter (someone) from', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('deteriorate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('determination', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('devastate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('develop', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('deviate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('devote', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('diagnose as', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('differentiate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('diminish', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('direct', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('disappearance', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('disclose', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('discover', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('discriminate (against)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('discriminate against', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('discuss about', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('disease', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('dismiss', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('dismissal', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('dispatch', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('display', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('displeased', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('dispose of', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('dispute', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('disqualify', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('disseminate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('distinct', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('distinguish', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('distort', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('distress', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('distribute', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('divert', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('dizzy', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('docile (dosayl)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('dominate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('donate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('donation', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('dowry', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('dramatic', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('draw', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('dress code', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('drug addict', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('drug dealer', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('dustbin', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('earth*****', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('edit', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('edition', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('educate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('effect', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('elect', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('eliminate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('elimination', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('embarrass', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('embrace', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('emerge', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('emphasize', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('employ', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('empty', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('emulate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('enable', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('enclose', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('encounter', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('encourage', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('endure', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('enhance', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('enhancement', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('enlarge', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('enquire', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('enslave', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('ensure', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('entertain', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('entirely', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('entrance', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('envy', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('epic', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('equal', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('equality', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('equate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('equip', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('erode', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('erupt', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('establish', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('estimate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('eternal', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('evaluate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('evaluation', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('evidently', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('evolve', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('exaggerated', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('excavate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('exceed', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('excessive', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('exchange', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('exclude', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('exclusive to', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('exclusively', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('excursion', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('exhibit', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('exist', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('existence', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('expand', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('expect', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('expectation', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('expense', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('experience', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('expire', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('Expiry Date', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('explode', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('exploit', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('explore', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('export', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('expose', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('express', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('extend', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('extract', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('extraordinary', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fabricate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('facilitate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fade', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('failure', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('faint', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fairly', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('falsify', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('familiar (with)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('famish', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fare', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fatal', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('favourable', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fearful for', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fertilize', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fetch', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fiancé', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fiancée', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('field trip', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fierce', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('figure', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('filthy', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('finance', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fine', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('firework', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fit', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('flatmate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('flattery', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('flee', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fleece', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('flight', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('flow', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fluctuate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('focus on', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fold', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('force', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('forceful', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('forecast', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('forge', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('forgery', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('former', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('formerly', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('formulate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('forthcoming', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fortify', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fracture', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('frail', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('frame', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('freed', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fulfil', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fundamental', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('funeral', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fussy', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('fuzzy', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('gather', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('gender', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('generate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('genre (', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('get rid of', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('giant', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('give up', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('glance', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('gloom', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('glorify', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('goal', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('govern', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('government', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('grab', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('gradually', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('grant', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('grasp', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('graveyard', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('groom', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('grow tired of', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('growl', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('guide', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('harass', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('harbour', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('hardship', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('harshly', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('hasten', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('havoc', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('hazard', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('hazardous', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('hectic', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('hesitate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('highly', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('hinder', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('hire', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('hitchhiker', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('hollow', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('hopefully', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('horrible', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('huge', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('humiliate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('hunter', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('hurricane (hörikeyn)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('iceberg', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('identify', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('idle', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('ignore', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('illusion', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('illustrate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('imagine', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('imitate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('immediate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('immobilize (immmobilayz)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('impact', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('impeach', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('implement', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('implicate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('imply', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('impose', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('imprisonment', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('improve', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('inaudible', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('incapable of (inkepıbıl)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('incapacitate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('incessant', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('incline', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('include', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('incorporate into', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('incredible', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('indicate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('indifference to', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('induce', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('inevitable', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('infer', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('influence', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('influential (influwenşıl)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('inherit', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('inhibit', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('initially', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('initiate (inişiyeyt)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('injure', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('injustice', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('innovate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('innovation', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('innovative', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('insatiable (inseyşıbıl)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('insignificant', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('insist (on)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('inspect', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('instantaneously', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('institute', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('instruct', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('insulate (against)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('integrate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('intelligence', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('intention (intenşın)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('intentional', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('interaction (with)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('interfere', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('interfere with', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('interpretation', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('interrogate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('interview', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('intimate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('introduce', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('invade', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('invaluable', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('invent', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('invest (in)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('investigate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('invoke', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('involve', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('involvement', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('irregularity', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('isolate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('jeopardize (ciopidayz)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('join', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('joint', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('justify', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('kennel', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('keyhole', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('kidnapper', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('knock', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('knowledge', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('label', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('lamb', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('latter', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('lawyer', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('leak', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('legend', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('legislate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('leisure', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('lessen', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('levy', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('Likewise', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('listless', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('literacy', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('litter', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('loathe', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('locate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('location', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('loose', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('lovely', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('luggage (lagiç)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('magical (mecikıl)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('mainstream', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('maintain', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('make a decision', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('manage', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('management', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('manipulate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('manner', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('manufacture', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('march', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('massacre (messekı', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('master', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('masterpiece', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('mature (maçu', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('meadow', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('meander', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('measure (mejı', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('mediate between', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('meet', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('memorial', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('memory', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('merge', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('migrate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('minor', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('miraculously', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('misbehave', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('mischief', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('misunderstanding', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('mix up', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('mock at', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('modify', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('mood', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('mourning', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('move', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('movement', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('multinational', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('municipality', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('murder', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('mystery', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('narrowly', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('native to', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('neglect', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('nervous', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('neutrality (nötraliti)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('notice', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('obese', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('obey', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('objection', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('obligation', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('obscure', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('observe', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('obsolete', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('obtain', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('occasion', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('occasional', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('occupy', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('occur', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('occurrence', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('odd', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('Oddly enough!', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('odour', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('offend', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('offer', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('officially', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('opportunity', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('opposition', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('oppress', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('ordinary', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('originally', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('ornament', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('orphan', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('outcrop', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('outcry', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('outdo', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('outing', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('outlet', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('overlap', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('overlook', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('overtake', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('partially', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('participate in', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('participation', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('particular (pıtik', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('particularly', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('passenger', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('passionately', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('patiently', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('pavement', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('peace and quiet', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('peak', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('peculiar', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('pedestrian', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('penalize', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('perceive', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('permission', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('persevering', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('persist', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('persuade', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('pessimism', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('pet', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('pioneer', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('placement', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('plague (pleyg)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('plain', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('plead', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('please', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('pledge (plec)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('poem', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('point', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('policy', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('polio', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('pose', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('possess', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('possession', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('post', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('postpone', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('practically', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('praise', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('precede', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('predict', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('predictable', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('prejudice', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('present', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('preserve', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('pressure', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('prevent', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('previously', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('prior (to)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('prison', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('probability', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('process', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('progress', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('promote', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('prompt', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('promptly', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('proofread', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('properly', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('property', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('proportion', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('protection against', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('provoke', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('publish', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('purchase (pö', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('purchase', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('purpose', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('pursue', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('push', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('put forth', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('queue', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('race', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('racism', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('raid', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('raise', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('rate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('receive', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('reckless', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('recklessly', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('recognize', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('recommendation', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('referee', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('refreshing', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('refugee', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('refund', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('regard', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('regional', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('register', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('regret', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('regretful', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('regrettable', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('regularly', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('rehearse (rihörs)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('reject', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('rejection', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('relate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('release', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('relentless', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('relief', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('relocate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('reluctant (rilaktınt)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('remain', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('remark', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('remembrance', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('reminiscent of', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('remote', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('removal', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('remove', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('repeatedly', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('repetitive', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('replace (with)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('replica', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('request', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('require', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('requirement', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('resentful', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('reside', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('resident', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('resign from', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('resolve', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('resort', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('response', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('restlessness', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('result', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('reveal', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('revenge', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('revolve', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('reward', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('ride', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('rightfully', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('rise', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('rob somebody of something', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('robust (rıbast)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('rough (raf)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('rubble', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('sacrifice', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('salute', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('satisfaction', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('savage', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('scald', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('scalp', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('scarce', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('scarcely', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('scatter', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('sceptical', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('scratch', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('sculpture', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('seam', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('seasonal', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('secure', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('sedate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('seed', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('seize', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('sense', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('sentence', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('sentimental', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('session', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('sewage', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('shade', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('shortcoming', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('shorten', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('show off', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('shuffle', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('sigh', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('significant', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('silent', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('simply', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('simulate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('sink', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('situate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('size', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('skill', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('slaughter', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('slavery', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('sleeve', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('slight', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('slip', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('smash', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('smother (smadır)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('snap', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('soap', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('sociable', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('solely', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('soothing', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('spectacular', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('spend', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('spillage', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('spin', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('spine', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('spiritual', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('spoiled', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('spouse', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('spread', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('spring', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('stability', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('staff', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('stage', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('startle', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('statement', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('statue (steyçu)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('steadily', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('steal', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('stealthily (steltili)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('stem', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('stimulate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('stir', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('store', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('storm', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('stranger', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('stray', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('stress', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('stretch', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('strike', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('stroll', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('subject to', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('substantial', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('sue', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('sufficiently', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('suffrage', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('suggestion', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('suggestive of', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('suit', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('supply', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('support', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('supportive', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('suppress', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('surpass', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('surrender', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('suspend', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('suspicion', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('symptom', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('take off', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('take on', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('tame', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('tapestry', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('tasteful', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('tasty', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('temple (tempıl)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('tenderness', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('terminal', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('terminate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('territory', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('the rest of…', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('thoughtless', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('throughout', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('throw', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('throw out', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('thunderstorm', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('tomb', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('tough', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('trace', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('trade', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('traditional', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('trail', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('train', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('transmit', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('trash', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('treasure (trejı', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('treat', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('trick', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('trim', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('tripe', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('truthful about', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('turn in', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('unattended', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('unbearable', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('uncultured', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('undermine', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('undertake', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('unfortunate', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('unlimited', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('unreliable', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('untimely', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('unusual', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('unwind', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('upgrade', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('urgent', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('vacation', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('vague (veyg)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('valley', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('vanish', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('variety', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('vast', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('vet', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('vigorously', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('violate (vayoleyt)', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('violent', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('virtually', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('vocation', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('volunteer', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('vote for/against', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('voyage', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('wantonly', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('wear', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('weep', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('whirl', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('wholly', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('widely', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('widow', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('withdraw from', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('withdrawn', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('witness', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('worthless', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('yard', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('yield', 'YDS');
INSERT INTO word_exam_cross_ref
VALUES ('zip', 'YDS');
CREATE TABLE room_master_table
(
    id            INTEGER PRIMARY KEY,
    identity_hash TEXT
);
INSERT INTO room_master_table
VALUES (42, '20b0bef428aa7842b04322a5c7425ab1');
DELETE
FROM sqlite_sequence;
INSERT INTO sqlite_sequence
VALUES ('statistics', 0);
CREATE UNIQUE INDEX ` index_statistics_correct_count_wrong_count_skipped_count_learned ` ON ` statistics ` (` correct_count `, ` wrong_count `, ` skipped_count `, ` learned `);
COMMIT;