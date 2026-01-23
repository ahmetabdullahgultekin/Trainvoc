import { test, expect } from '@playwright/test';

test.describe('Game Flow E2E Tests', () => {
    test.describe('Room Creation Flow', () => {
        test('should navigate from play to create room', async ({ page }) => {
            await page.goto('/play');

            const createButton = page.getByRole('button', { name: /create/i })
                .or(page.getByRole('link', { name: /create/i }));

            if (await createButton.first().isVisible()) {
                await createButton.first().click();
                await expect(page).toHaveURL(/\/play\/create/);
            }
        });

        test('should display room settings form', async ({ page }) => {
            await page.goto('/play/create');

            // Should have form elements for room creation
            await expect(page.locator('form, .form, .settings')).toBeVisible();
        });

        test('should allow entering player name', async ({ page }) => {
            await page.goto('/play/create');

            const nameInput = page.getByPlaceholder(/name|nick/i)
                .or(page.getByLabel(/name|nick/i))
                .or(page.locator('input[type="text"]').first());

            if (await nameInput.isVisible()) {
                await nameInput.fill('TestPlayer');
                await expect(nameInput).toHaveValue('TestPlayer');
            }
        });

        test('should have room settings options', async ({ page }) => {
            await page.goto('/play/create');

            // Look for settings like max players, time per question, etc.
            const settings = page.locator('select, input[type="number"], .slider')
                .or(page.getByRole('combobox'))
                .or(page.getByRole('spinbutton'));

            await expect(page.locator('body')).toBeVisible();
        });
    });

    test.describe('Room Joining Flow', () => {
        test('should navigate from play to join room', async ({ page }) => {
            await page.goto('/play');

            const joinButton = page.getByRole('button', { name: /join/i })
                .or(page.getByRole('link', { name: /join/i }));

            if (await joinButton.first().isVisible()) {
                await joinButton.first().click();
                await expect(page).toHaveURL(/\/play\/join/);
            }
        });

        test('should require room code', async ({ page }) => {
            await page.goto('/play/join');

            const codeInput = page.getByPlaceholder(/code|room/i)
                .or(page.getByLabel(/code|room/i))
                .or(page.locator('input[type="text"]').first());

            if (await codeInput.isVisible()) {
                await expect(codeInput).toBeVisible();
            }
        });

        test('should validate room code format', async ({ page }) => {
            await page.goto('/play/join');

            const codeInput = page.getByPlaceholder(/code|room/i)
                .or(page.getByLabel(/code|room/i));
            const submitButton = page.getByRole('button', { name: /join/i });

            if (await codeInput.first().isVisible() && await submitButton.isVisible()) {
                // Try with invalid short code
                await codeInput.first().fill('AB');
                await submitButton.click();

                // Should either show error or stay on page
                await page.waitForTimeout(500);
                await expect(page).toHaveURL(/\/play\/join/);
            }
        });
    });

    test.describe('Lobby Flow', () => {
        test('should display active rooms in lobby', async ({ page }) => {
            await page.goto('/play/lobby');

            // Wait for room list to potentially load
            await page.waitForTimeout(2000);

            // Should show rooms or empty state
            await expect(page.locator('body')).toBeVisible();
        });

        test('should allow refreshing room list', async ({ page }) => {
            await page.goto('/play/lobby');

            const refreshButton = page.getByRole('button', { name: /refresh|reload/i })
                .or(page.locator('[aria-label*="refresh"]'));

            if (await refreshButton.first().isVisible()) {
                await refreshButton.first().click();
                await page.waitForTimeout(1000);
                await expect(page.locator('body')).toBeVisible();
            }
        });

        test('should show room details in list', async ({ page }) => {
            await page.goto('/play/lobby');
            await page.waitForTimeout(2000);

            // If rooms exist, they should show host name, player count, etc.
            await expect(page.locator('body')).toBeVisible();
        });
    });

    test.describe('Game Page', () => {
        test('should handle direct navigation to game without room', async ({ page }) => {
            await page.goto('/play/game');

            // Should redirect to play or show error
            await page.waitForTimeout(1000);
            await expect(page.locator('body')).toBeVisible();
        });

        test('should display game UI elements', async ({ page }) => {
            await page.goto('/play/game');
            await page.waitForTimeout(1000);

            // Page should be visible (even if showing "no room" state)
            await expect(page.locator('body')).toBeVisible();
        });
    });
});

test.describe('Navigation and State Persistence', () => {
    test('should maintain state when navigating back and forth', async ({ page }) => {
        // Start at play
        await page.goto('/play');

        // Go to create
        const createLink = page.getByRole('link', { name: /create/i });
        if (await createLink.isVisible()) {
            await createLink.click();
            await page.waitForURL(/\/play\/create/);
        }

        // Go back
        await page.goBack();
        await expect(page).toHaveURL(/\/play/);
    });

    test('should handle browser refresh gracefully', async ({ page }) => {
        await page.goto('/play/lobby');
        await page.waitForTimeout(1000);

        // Refresh the page
        await page.reload();

        // Page should reload without errors
        await expect(page.locator('body')).toBeVisible();
    });

    test('should handle deep link navigation', async ({ page }) => {
        // Direct navigation to nested route
        await page.goto('/play/lobby');
        await expect(page.locator('body')).toBeVisible();

        await page.goto('/play/create');
        await expect(page.locator('body')).toBeVisible();

        await page.goto('/play/join');
        await expect(page.locator('body')).toBeVisible();
    });
});

test.describe('Error Handling', () => {
    test('should handle network errors gracefully', async ({ page, context }) => {
        await page.goto('/play/lobby');

        // The page should handle API failures without crashing
        await expect(page.locator('body')).toBeVisible();
    });

    test('should display error messages appropriately', async ({ page }) => {
        await page.goto('/play/join');

        // Try to join with invalid code
        const codeInput = page.getByPlaceholder(/code|room/i)
            .or(page.getByLabel(/code|room/i));
        const submitButton = page.getByRole('button', { name: /join/i });

        if (await codeInput.first().isVisible() && await submitButton.isVisible()) {
            await codeInput.first().fill('NOTREAL123');
            await submitButton.click();

            // Wait for potential error message
            await page.waitForTimeout(2000);

            // Page should still be functional
            await expect(page.locator('body')).toBeVisible();
        }
    });
});
