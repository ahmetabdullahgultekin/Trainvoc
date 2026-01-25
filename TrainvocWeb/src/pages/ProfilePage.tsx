import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { User, Save } from 'lucide-react'
import { Card } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'
import useProfile from '../components/shared/useProfile'

const avatarList = [
  '🦊', '🐱', '🐶', '🐵', '🐸', '🐼', '🐧', '🐯', '🦁', '🐮',
  '🐨', '🐰', '🐻', '🐷', '🐔', '🦄', '🐙', '🐢', '🐳', '🐝'
]

const ProfilePage: React.FC = () => {
  const { t } = useTranslation()
  const { nick, setNick, avatar, setAvatar } = useProfile()
  const [tempNick, setTempNick] = useState(nick)
  const [tempAvatar, setTempAvatar] = useState(
    avatar !== undefined && avatar !== null && avatar !== '' && !isNaN(Number(avatar))
      ? Number(avatar)
      : 0
  )

  const handleSave = () => {
    setNick(tempNick)
    setAvatar(tempAvatar.toString())
  }

  return (
    <div className="max-w-md mx-auto mt-6 px-4">
      <Card className="p-6">
        <div className="flex items-center gap-2 mb-6">
          <User className="h-6 w-6 text-brand-500" />
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
            {t('profile.editProfile', 'Edit Profile')}
          </h1>
        </div>

        <div className="flex flex-col items-center mb-6">
          <div className="w-20 h-20 rounded-full bg-brand-100 dark:bg-brand-900/30 flex items-center justify-center text-5xl mb-4">
            {avatarList[tempAvatar] || '🦊'}
          </div>

          <div className="grid grid-cols-5 sm:grid-cols-10 gap-2">
            {avatarList.map((emoji, index) => (
              <button
                key={emoji}
                onClick={() => setTempAvatar(index)}
                className={cn(
                  'w-10 h-10 rounded-lg text-xl flex items-center justify-center transition-all',
                  tempAvatar === index
                    ? 'bg-brand-500 text-white shadow-md scale-110'
                    : 'bg-gray-100 dark:bg-gray-800 hover:bg-gray-200 dark:hover:bg-gray-700'
                )}
              >
                {emoji}
              </button>
            ))}
          </div>
        </div>

        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              {t('profile.nickname', 'Nickname')}
            </label>
            <Input
              value={tempNick}
              onChange={e => setTempNick(e.target.value)}
              placeholder={t('profile.nicknamePlaceholder', 'Enter nickname...')}
            />
          </div>

          <Button onClick={handleSave} className="w-full" size="lg">
            <Save className="h-4 w-4 mr-2" />
            {t('profile.save', 'Save')}
          </Button>
        </div>
      </Card>
    </div>
  )
}

export default ProfilePage
