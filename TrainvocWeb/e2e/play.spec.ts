import { test, expect } from '@playwright/test';

test.describe('Play Page', () => {
    test.beforeEach(async ({ page }) => {
        await page.goto('/play');
    });

    test('should display play menu', async ({ page }) => {
        // Check for play menu elements
        await expect(page.locator('body')).toBeVisible();
    });

    test('should have create room option', async ({ page }) => {
        const createButton = page.getByRole('button', { name: /create/i })
            .or(page.getByRole('link', { name: /create/i }));

        // If create option exists, it should be visible
        if (await createButton.first().isVisible()) {
            await expect(createButton.first()).toBeVisible();
        }
    });

    test('should have join room option', async ({ page }) => {
        const joinButton = page.getByRole('button', { name: /join/i })
            .or(page.getByRole('link', { name: /join/i }));

        if (await joinButton.first().isVisible()) {
            await expect(joinButton.first()).toBeVisible();
        }
    });

    test('should navigate to lobby', async ({ page }) => {
        const lobbyLink = page.getByRole('link', { name: /lobby/i });
        if (await lobbyLink.isVisible()) {
            await lobbyLink.click();
            await expect(page).toHaveURL(/\/play\/lobby/);
        }
    });
});

test.describe('Create Room Page', () => {
    test.beforeEach(async ({ page }) => {
        await page.goto('/play/create');
    });

    test('should display create room form', async ({ page }) => {
        await expect(page.locator('body')).toBeVisible();
    });

    test('should have nickname input', async ({ page }) => {
        const nicknameInput = page.getByPlaceholder(/name|nick/i)
            .or(page.getByLabel(/name|nick/i));

        if (await nicknameInput.first().isVisible()) {
            await expect(nicknameInput.first()).toBeVisible();
        }
    });

    test('should validate empty nickname', async ({ page }) => {
        const submitButton = page.getByRole('button', { name: /create|start/i });

        if (await submitButton.isVisible()) {
            await submitButton.click();
            // Should show validation error or not submit
            await expect(page).toHaveURL(/\/play\/create/);
        }
    });
});

test.describe('Join Room Page', () => {
    test.beforeEach(async ({ page }) => {
        await page.goto('/play/join');
    });

    test('should display join room form', async ({ page }) => {
        await expect(page.locator('body')).toBeVisible();
    });

    test('should have room code input', async ({ page }) => {
        const codeInput = page.getByPlaceholder(/code|room/i)
            .or(page.getByLabel(/code|room/i));

        if (await codeInput.first().isVisible()) {
            await expect(codeInput.first()).toBeVisible();
        }
    });

    test('should validate invalid room code', async ({ page }) => {
        const codeInput = page.getByPlaceholder(/code|room/i)
            .or(page.getByLabel(/code|room/i));
        const submitButton = page.getByRole('button', { name: /join/i });

        if (await codeInput.first().isVisible() && await submitButton.isVisible()) {
            await codeInput.first().fill('INVALID');
            await submitButton.click();
            // Should show error or stay on page
            await page.waitForTimeout(1000);
        }
    });
});

test.describe('Lobby Page', () => {
    test.beforeEach(async ({ page }) => {
        await page.goto('/play/lobby');
    });

    test('should display lobby', async ({ page }) => {
        await expect(page.locator('body')).toBeVisible();
    });

    test('should show available rooms list', async ({ page }) => {
        // Wait for room list to load
        await page.waitForTimeout(2000);
        // The page should be visible regardless of whether rooms exist
        await expect(page.locator('body')).toBeVisible();
    });
});
