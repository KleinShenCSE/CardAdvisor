# CardAdvisor

A native Android app that uses your camera and Gemini Vision AI to identify what you're about to buy, then recommends the best credit card in your wallet to maximize rewards.

## How it works

1. **Snap a photo** of a product, price tag, or store
2. **Gemini Vision** identifies the purchase category (groceries, dining, travel, gas, etc.)
3. **CardAdvisor** ranks your cards by effective cashback or points value for that category

## Features

- **AI-powered recognition** — Gemini 1.5 Flash identifies purchase categories from photos
- **Cashback & points support** — compares cards by effective cashback %, converting points using your configured cents-per-point value
- **Card management** — add, edit, and delete cards with per-category reward rates
- **Card color picker** — customize each card with a color palette + live preview
- **Swipe to delete** — swipe left on a card with haptic feedback to remove it
- **Fully local** — card data stored on-device with Room DB, no account required

## Tech stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Camera | CameraX |
| AI | Gemini 1.5 Flash (Vision API) |
| Database | Room |
| Async | Coroutines + StateFlow |
| Navigation | Navigation Compose |

## Setup

### 1. Clone the repo

```bash
git clone https://github.com/KleinShenCSE/CardAdvisor.git
cd CardAdvisor
```

### 2. Get a Gemini API key

1. Go to [aistudio.google.com/apikey](https://aistudio.google.com/apikey)
2. Create a new API key (free tier available)

### 3. Add the key to `local.properties`

```properties
GEMINI_API_KEY=your_key_here
```

> `local.properties` is gitignored — your key won't be committed.

### 4. Open in Android Studio and run

Requires Android Studio Hedgehog or later. Min SDK 26 (Android 8.0).

## Project structure

```
app/src/main/java/com/example/cardadvisor/
├── MainActivity.kt              # Nav host + bottom bar
├── api/
│   └── GeminiService.kt         # Gemini Vision API call
├── data/
│   ├── db/                      # Room entities, DAO, database
│   └── repository/              # CardRepository
├── domain/
│   ├── Category.kt              # Purchase categories enum
│   ├── RewardType.kt            # CASHBACK | POINTS
│   └── RecommendationEngine.kt  # Ranking logic
└── ui/
    ├── camera/                  # Camera capture + ViewModel
    ├── cards/                   # Cards list, add/edit screens
    ├── components/              # Shared CardPreview composable
    └── result/                  # Recommendation results screen
```

## Supported categories

Groceries · Dining · Travel · Gas · Streaming · Entertainment · Pharmacy · Shopping · Other
