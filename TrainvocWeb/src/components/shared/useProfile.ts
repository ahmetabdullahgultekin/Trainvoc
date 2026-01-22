import {useState} from 'react';

const NICK_KEY = 'trainvoc_nick';
const AVATAR_KEY = 'trainvoc_avatar';

export function getStoredNick() {
    return localStorage.getItem(NICK_KEY) || '';
}

export function setStoredNick(nick: string) {
    localStorage.setItem(NICK_KEY, nick);
}

export function getStoredAvatar() {
    return localStorage.getItem(AVATAR_KEY) || '';
}

export function setStoredAvatar(avatar: string) {
    localStorage.setItem(AVATAR_KEY, avatar);
}

export interface Player {
    id: string;
    name: string;
    avatar: string;
}

export function getStoredPlayer(): Player {
    return {
        id: localStorage.getItem('trainvoc_player_id') || '',
        name: getStoredNick(),
        avatar: getStoredAvatar(),
    };
}

export function setStoredPlayer(player: Player) {
    localStorage.setItem('trainvoc_player_id', player.id);
    setStoredNick(player.name);
    setStoredAvatar(player.avatar);
}

export default function useProfile() {
    const [nick, setNickState] = useState(getStoredNick());
    const [avatar, setAvatarState] = useState(getStoredAvatar());
    const [id, setIdState] = useState(localStorage.getItem('trainvoc_player_id') || '');

    const setNick = (newNick: string) => {
        setStoredNick(newNick);
        setNickState(newNick);
    };

    const setAvatar = (newAvatar: string) => {
        setStoredAvatar(newAvatar);
        setAvatarState(newAvatar);
    };

    const setId = (newId: string) => {
        localStorage.setItem('trainvoc_player_id', newId);
        setIdState(newId);
    };

    const setPlayer = (player: Player) => {
        setStoredPlayer(player);
        setIdState(player.id);
        setNickState(player.name);
        setAvatarState(player.avatar);
    };

    return {id, setId, nick, setNick, avatar, setAvatar, player: {id, name: nick, avatar}, setPlayer};
}

const avatarList = [
    '🦊', '🐱', '🐶', '🐵', '🐸', '🐼', '🐧', '🐯', '🦁', '🐮', '🐨', '🐰', '🐻', '🐷', '🐔', '🦄', '🐙', '🐢', '🐳', '🐝'
];

export {avatarList};
