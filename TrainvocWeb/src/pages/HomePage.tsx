import { HeroSection, FeaturesGrid, HowItWorks, CTASection } from '@/components/landing'
import { Header, Footer } from '@/components/layout'

function HomePage() {
  return (
    <div className="min-h-screen">
      <Header />
      <main>
        <HeroSection />
        <FeaturesGrid />
        <HowItWorks />
        <CTASection />
      </main>
      <Footer />
    </div>
  )
}

export default HomePage
