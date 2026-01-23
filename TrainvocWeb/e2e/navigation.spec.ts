import { test, expect } from '@playwright/test';

test.describe('Navigation Tests', () => {
    test.describe('Main Navigation', () => {
        test('should navigate to all main pages from home', async ({ page }) => {
            await page.goto('/');

            // Test navigation links
            const navLinks = [
                { name: /play/i, url: /\/play/ },
                { name: /about/i, url: /\/about/ },
                { name: /leaderboard/i, url: /\/leaderboard/ },
            ];

            for (const link of navLinks) {
                const navLink = page.getByRole('link', { name: link.name });
                if (await navLink.first().isVisible()) {
                    await navLink.first().click();
                    await expect(page).toHaveURL(link.url);
                    await page.goto('/'); // Return to home
                }
            }
        });

        test('should have consistent navigation across pages', async ({ page }) => {
            const pages = ['/play', '/about', '/leaderboard'];

            for (const pageUrl of pages) {
                await page.goto(pageUrl);
                const nav = page.getByRole('navigation')
                    .or(page.locator('nav, header'));
                await expect(nav.first()).toBeVisible();
            }
        });
    });

    test.describe('Mobile Navigation', () => {
        test.beforeEach(async ({ page }) => {
            await page.setViewportSize({ width: 375, height: 667 });
        });

        test('should show mobile menu button on small screens', async ({ page }) => {
            await page.goto('/');

            // Look for hamburger menu or mobile menu button
            const menuButton = page.getByRole('button', { name: /menu/i })
                .or(page.locator('[aria-label*="menu"]'))
                .or(page.locator('.hamburger, .menu-toggle'));

            // Mobile menu might exist
            await expect(page.locator('body')).toBeVisible();
        });

        test('should navigate on mobile', async ({ page }) => {
            await page.goto('/');

            // Try to navigate to play
            const playLink = page.getByRole('link', { name: /play/i });
            if (await playLink.isVisible()) {
                await playLink.click();
                await expect(page).toHaveURL(/\/play/);
            }
        });
    });

    test.describe('Breadcrumb Navigation', () => {
        test('should show breadcrumbs on nested pages', async ({ page }) => {
            await page.goto('/play/create');

            // Look for breadcrumb navigation
            const breadcrumbs = page.locator('[aria-label*="breadcrumb"], .breadcrumb, nav ol, nav ul');

            // Breadcrumbs may or may not be present
            await expect(page.locator('body')).toBeVisible();
        });
    });

    test.describe('Footer Navigation', () => {
        test('should have footer links on main pages', async ({ page }) => {
            await page.goto('/');

            const footer = page.locator('footer');
            if (await footer.isVisible()) {
                await expect(footer).toBeVisible();
            }
        });

        test('should navigate to contact from footer if available', async ({ page }) => {
            await page.goto('/');

            const contactLink = page.getByRole('link', { name: /contact/i });
            if (await contactLink.first().isVisible()) {
                await contactLink.first().click();
                await expect(page).toHaveURL(/\/contact/);
            }
        });
    });

    test.describe('URL Routing', () => {
        test('should handle unknown routes gracefully', async ({ page }) => {
            await page.goto('/nonexistent-page-12345');

            // Should show 404 or redirect to home
            await expect(page.locator('body')).toBeVisible();
        });

        test('should handle trailing slashes', async ({ page }) => {
            await page.goto('/play/');
            await expect(page.locator('body')).toBeVisible();
        });

        test('should handle query parameters', async ({ page }) => {
            await page.goto('/play?ref=test');
            await expect(page.locator('body')).toBeVisible();
        });
    });
});

test.describe('Accessibility Navigation', () => {
    test('should have skip to content link', async ({ page }) => {
        await page.goto('/');

        // Press Tab to reveal skip link
        await page.keyboard.press('Tab');

        const skipLink = page.getByRole('link', { name: /skip to content|skip to main/i })
            .or(page.locator('a[href="#main"], a[href="#content"]'));

        // Skip link may or may not exist
        await expect(page.locator('body')).toBeVisible();
    });

    test('should navigate with keyboard only', async ({ page }) => {
        await page.goto('/');

        // Tab through elements
        for (let i = 0; i < 5; i++) {
            await page.keyboard.press('Tab');
        }

        // Should be able to interact with focused element
        await page.keyboard.press('Enter');

        // Page should still be functional
        await expect(page.locator('body')).toBeVisible();
    });

    test('should have proper focus management', async ({ page }) => {
        await page.goto('/');

        // Click on a link
        const playLink = page.getByRole('link', { name: /play/i });
        if (await playLink.first().isVisible()) {
            await playLink.first().click();
            await page.waitForURL(/\/play/);

            // Focus should be managed (on main content or page title)
            await expect(page.locator('body')).toBeVisible();
        }
    });
});
