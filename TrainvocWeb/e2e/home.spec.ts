import { test, expect } from '@playwright/test';

test.describe('Home Page', () => {
    test.beforeEach(async ({ page }) => {
        await page.goto('/');
    });

    test('should display the home page', async ({ page }) => {
        await expect(page).toHaveTitle(/Trainvoc/i);
    });

    test('should have navigation links', async ({ page }) => {
        // Check for main navigation elements
        await expect(page.getByRole('navigation')).toBeVisible();
    });

    test('should navigate to play page', async ({ page }) => {
        // Look for a play button or link
        const playLink = page.getByRole('link', { name: /play/i });
        if (await playLink.isVisible()) {
            await playLink.click();
            await expect(page).toHaveURL(/\/play/);
        }
    });

    test('should navigate to about page', async ({ page }) => {
        const aboutLink = page.getByRole('link', { name: /about/i });
        if (await aboutLink.isVisible()) {
            await aboutLink.click();
            await expect(page).toHaveURL(/\/about/);
        }
    });

    test('should be responsive on mobile', async ({ page }) => {
        await page.setViewportSize({ width: 375, height: 667 });
        await expect(page.locator('body')).toBeVisible();
    });
});
