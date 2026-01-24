import { useEffect, useState } from 'react'
import { Maximize, Minimize } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { isFullscreen as checkFullscreen, toggleFullscreen, onFullscreenChange } from '../../utils/fullscreen'

const FullscreenButton: React.FC = () => {
  const [isFullscreen, setIsFullscreen] = useState(checkFullscreen())

  useEffect(() => {
    return onFullscreenChange(setIsFullscreen)
  }, [])

  const handleToggleFullscreen = () => {
    toggleFullscreen()
  }

  return (
    <Button
      variant="outline"
      size="icon"
      onClick={handleToggleFullscreen}
      className="fixed top-4 left-4 z-[12000] bg-white shadow-md hover:bg-gray-100"
      title={isFullscreen ? 'Tam ekrandan çık' : 'Tam ekrana geç'}
    >
      {isFullscreen ? (
        <Minimize className="h-5 w-5" />
      ) : (
        <Maximize className="h-5 w-5" />
      )}
    </Button>
  )
}

export default FullscreenButton
