import {useState} from 'react';

const NICK_KEY = 'trainvoc_nick';

export function getStoredNick() {
    return localStorage.getItem(NICK_KEY) || '';
}

export function setStoredNick(nick: string) {
    localStorage.setItem(NICK_KEY, nick);
}

export default function useNick() {
    const [nick, setNickState] = useState(getStoredNick());

    const setNick = (newNick: string) => {
        setStoredNick(newNick);
        setNickState(newNick);
    };

    return [nick, setNick] as const;
}

