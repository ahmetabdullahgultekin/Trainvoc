import React from 'react';
import {useTranslation} from 'react-i18next';
import styles from './Footer.module.css';

const Footer: React.FC = () => {
    const {t} = useTranslation();
    return (
        <footer className={styles.footer}>
            <span>{t('footer')}</span>
        </footer>
    );
};

export default Footer;
