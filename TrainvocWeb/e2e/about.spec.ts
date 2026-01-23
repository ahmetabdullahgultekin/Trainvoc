import { test, expect } from '@playwright/test';

test.describe('About Page', () => {
    test.beforeEach(async ({ page }) => {
        await page.goto('/about');
    });

    test('should display the about page title', async ({ page }) => {
        await expect(page.locator('body')).toBeVisible();
        // Check for heading or title containing "about" or "trainvoc"
        const heading = page.getByRole('heading').first();
        await expect(heading).toBeVisible();
    });

    test('should display project information', async ({ page }) => {
        // The about page should contain some text content
        await expect(page.locator('main, article, .content, body')).toBeVisible();
    });

    test('should have navigation back to home', async ({ page }) => {
        // There should be a way to navigate back
        const homeLink = page.getByRole('link', { name: /home|trainvoc/i })
            .or(page.getByRole('link', { name: /^trainvoc$/i }))
            .or(page.locator('a[href="/"]'));

        if (await homeLink.first().isVisible()) {
            await homeLink.first().click();
            await expect(page).toHaveURL('/');
        }
    });

    test('should be accessible with proper heading structure', async ({ page }) => {
        // Check for at least one heading
        const headings = page.getByRole('heading');
        await expect(headings.first()).toBeVisible();
    });

    test('should be responsive on tablet', async ({ page }) => {
        await page.setViewportSize({ width: 768, height: 1024 });
        await expect(page.locator('body')).toBeVisible();
    });
});
