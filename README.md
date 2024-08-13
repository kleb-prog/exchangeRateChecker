# ExchangeRateChecker

Currency rate monitoring application with telegram integration. Subscribe to interesting currency rates and be notified when rates change.
Filter out small currency fluctuations by configuring a delta limit.
All the interactions will be with the telegram bot.

## Features

### User Features
- Subscribe to an unlimited number of currency pairs
- Receive notifications when exchange rates change
- Check currency rates instantly using a special command in the Telegram bot

### Administrative Features
- User Interface for bot administration
- View list of users and their subscriptions
- Send messages to all chat users simultaneously

## Requirements
All you need to use it is to provide an API key to the [ExchangeRateAPI](https://app.exchangerate-api.com) service.
And the Token for the Telegram bot [TelegramBotAPI](https://core.telegram.org/bots/features#creating-a-new-bot).
For that, create a new file private.properties in the repository root folder and set:
- 'exchangeApiKey' variable with your own key from ExchangeRateApi service,
- 'telegramToken' variable with your telegram bot token.