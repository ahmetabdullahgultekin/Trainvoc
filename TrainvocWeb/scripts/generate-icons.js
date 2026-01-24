/**
 * Generate PWA icons from the source launcher icon.
 * Run with: node scripts/generate-icons.js
 */
import sharp from 'sharp';
import { mkdir } from 'fs/promises';
import { dirname, join } from 'path';
import { fileURLToPath } from 'url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const projectRoot = join(__dirname, '..');

const SOURCE_ICON = join(projectRoot, '..', 'TrainvocClient', 'app', 'src', 'main', 'ic_launcher-playstore.png');
const OUTPUT_DIR = join(projectRoot, 'public', 'icons');

const ICON_SIZES = [72, 96, 128, 144, 152, 192, 384, 512];

async function generateIcons() {
    // Ensure output directory exists
    await mkdir(OUTPUT_DIR, { recursive: true });

    console.log(`Generating icons from: ${SOURCE_ICON}`);
    console.log(`Output directory: ${OUTPUT_DIR}`);

    for (const size of ICON_SIZES) {
        const outputPath = join(OUTPUT_DIR, `icon-${size}.png`);

        await sharp(SOURCE_ICON)
            .resize(size, size, {
                fit: 'contain',
                background: { r: 15, g: 15, b: 35, alpha: 1 } // Match theme background
            })
            .png()
            .toFile(outputPath);

        console.log(`Generated: icon-${size}.png`);
    }

    // Generate shortcut icons
    const shortcutSizes = ['play-icon', 'create-icon'];
    for (const name of shortcutSizes) {
        const outputPath = join(OUTPUT_DIR, `${name}.png`);

        await sharp(SOURCE_ICON)
            .resize(96, 96, {
                fit: 'contain',
                background: { r: 15, g: 15, b: 35, alpha: 1 }
            })
            .png()
            .toFile(outputPath);

        console.log(`Generated: ${name}.png`);
    }

    console.log('Done!');
}

generateIcons().catch(console.error);
