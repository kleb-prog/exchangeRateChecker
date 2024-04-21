# ExchangeRateChecker

Simple application to check the latest exchange rates of different currencies. For now it is USD to RUB.

## Requirements
All you need to use it is to provide an API key to the [ExchangeRateAPI](https://app.exchangerate-api.com) service.
And the Token for the Telgram bot [TelegramBotAPI](https://core.telegram.org/bots/features#creating-a-new-bot).
For that, create a new file private.properties in the recourses folder and set: 
 - 'exchangeApiKey' variable with your own key from ExchangeRateApi service,
 - 'telegramToken' variable with your telegram bot token.