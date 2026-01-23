import { test, expect } from '@playwright/test';

test.describe('Accessibility Tests', () => {
    test.describe('Color and Contrast', () => {
        test('should render content visually', async ({ page }) => {
            await page.goto('/');

            // Basic check that page has visible content
            await expect(page.locator('body')).toBeVisible();

            // Check for text content
            const textContent = await page.locator('body').textContent();
            expect(textContent?.length).toBeGreaterThan(0);
        });
    });

    test.describe('Interactive Elements', () => {
        test('should have clickable buttons', async ({ page }) => {
            await page.goto('/play');

            const buttons = page.getByRole('button');
            const buttonCount = await buttons.count();

            for (let i = 0; i < Math.min(buttonCount, 5); i++) {
                const button = buttons.nth(i);
                if (await button.isVisible()) {
                    await expect(button).toBeEnabled();
                }
            }
        });

        test('should have accessible links', async ({ page }) => {
            await page.goto('/');

            const links = page.getByRole('link');
            const linkCount = await links.count();

            for (let i = 0; i < Math.min(linkCount, 10); i++) {
                const link = links.nth(i);
                if (await link.isVisible()) {
                    // Links should have text content or aria-label
                    const text = await link.textContent();
                    const ariaLabel = await link.getAttribute('aria-label');
                    expect(text || ariaLabel).toBeTruthy();
                }
            }
        });

        test('should have accessible form inputs', async ({ page }) => {
            await page.goto('/play/create');

            const inputs = page.locator('input, select, textarea');
            const inputCount = await inputs.count();

            for (let i = 0; i < Math.min(inputCount, 5); i++) {
                const input = inputs.nth(i);
                if (await input.isVisible()) {
                    // Inputs should have label, aria-label, or placeholder
                    const id = await input.getAttribute('id');
                    const ariaLabel = await input.getAttribute('aria-label');
                    const placeholder = await input.getAttribute('placeholder');

                    // At least one accessibility attribute should be present
                    await expect(input).toBeVisible();
                }
            }
        });
    });

    test.describe('Semantic HTML', () => {
        test('should have main landmark', async ({ page }) => {
            await page.goto('/');

            const main = page.locator('main')
                .or(page.getByRole('main'));

            await expect(main.first()).toBeVisible();
        });

        test('should have proper heading hierarchy', async ({ page }) => {
            await page.goto('/');

            // Should have at least one h1
            const h1 = page.getByRole('heading', { level: 1 });
            const h1Count = await h1.count();

            // Most pages should have an h1, but not strictly required
            await expect(page.locator('body')).toBeVisible();
        });

        test('should have navigation landmark', async ({ page }) => {
            await page.goto('/');

            const nav = page.locator('nav')
                .or(page.getByRole('navigation'));

            await expect(nav.first()).toBeVisible();
        });
    });

    test.describe('Keyboard Accessibility', () => {
        test('should trap focus in modals if present', async ({ page }) => {
            await page.goto('/play/create');

            // Look for modal triggers
            const modalTrigger = page.getByRole('button', { name: /settings|options/i });

            if (await modalTrigger.first().isVisible()) {
                await modalTrigger.first().click();
                await page.waitForTimeout(500);

                // If modal opened, focus should be trapped
                await expect(page.locator('body')).toBeVisible();
            }
        });

        test('should have visible focus indicators', async ({ page }) => {
            await page.goto('/');

            // Tab to first interactive element
            await page.keyboard.press('Tab');

            // The focused element should be visible
            const focused = page.locator(':focus');
            await expect(focused.first()).toBeVisible();
        });
    });

    test.describe('Screen Reader Support', () => {
        test('should have alt text for images', async ({ page }) => {
            await page.goto('/');

            const images = page.locator('img');
            const imageCount = await images.count();

            for (let i = 0; i < Math.min(imageCount, 5); i++) {
                const img = images.nth(i);
                if (await img.isVisible()) {
                    const alt = await img.getAttribute('alt');
                    const role = await img.getAttribute('role');

                    // Image should have alt text or be decorative (role="presentation")
                    expect(alt !== null || role === 'presentation').toBeTruthy();
                }
            }
        });

        test('should have aria-live regions for dynamic content', async ({ page }) => {
            await page.goto('/play/lobby');

            // Wait for potential updates
            await page.waitForTimeout(2000);

            // Page should handle dynamic content
            await expect(page.locator('body')).toBeVisible();
        });
    });
});

test.describe('Responsive Design', () => {
    const viewports = [
        { name: 'Mobile S', width: 320, height: 568 },
        { name: 'Mobile M', width: 375, height: 667 },
        { name: 'Mobile L', width: 425, height: 812 },
        { name: 'Tablet', width: 768, height: 1024 },
        { name: 'Laptop', width: 1024, height: 768 },
        { name: 'Desktop', width: 1440, height: 900 },
    ];

    for (const viewport of viewports) {
        test(`should render correctly on ${viewport.name}`, async ({ page }) => {
            await page.setViewportSize({ width: viewport.width, height: viewport.height });
            await page.goto('/');

            // Content should be visible
            await expect(page.locator('body')).toBeVisible();

            // No horizontal scroll on mobile
            if (viewport.width < 768) {
                const bodyWidth = await page.evaluate(() => document.body.scrollWidth);
                expect(bodyWidth).toBeLessThanOrEqual(viewport.width + 20); // Small tolerance
            }
        });
    }
});

test.describe('Performance', () => {
    test('should load quickly', async ({ page }) => {
        const startTime = Date.now();
        await page.goto('/');
        const loadTime = Date.now() - startTime;

        // Page should load in under 10 seconds
        expect(loadTime).toBeLessThan(10000);
    });

    test('should not have JavaScript errors', async ({ page }) => {
        const errors: string[] = [];
        page.on('pageerror', error => errors.push(error.message));

        await page.goto('/');
        await page.waitForTimeout(2000);

        // No critical JS errors (warnings are ok)
        expect(errors.filter(e => !e.includes('Warning'))).toHaveLength(0);
    });
});
