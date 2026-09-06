# Nobitex Trader

اپلیکیشن اندرویدی کنترل ربات معامله‌گری.

## معماری

Android App → API Server → Trading Bot → Nobitex

## امکانات

- اتصال به سرور
- نمایش وضعیت ربات
- نمایش موجودی کیف پول
- همگام‌سازی کیف پول
- شروع ربات
- توقف ربات
- توقف اضطراری
- نمایش معاملات
- نمایش گزارش فعالیت

## امنیت

کلید کنترل با Android Keystore و AES-GCM ذخیره می‌شود.

هیچ IP، دامنه، API Key یا Secret واقعی داخل پروژه قرار داده نشده است.

## API

مسیرهای استفاده‌شده:

- `POST /api/bot/connect`
- `GET /api/bot/status`
- `POST /api/wallet/sync`
- `POST /api/wallet/balance`
- `POST /api/bot/allocate`
- `POST /api/bot/start`
- `POST /api/bot/stop`
- `POST /api/bot/emergency-stop`
- `GET /api/bot/trades`
- `GET /api/bot/logs`

سرور واقعی باید دقیقاً با قرارداد API برنامه سازگار باشد.

## Build

ساخت APK از طریق GitHub Actions انجام می‌شود.

خروجی:

`NobitexTrader-debug-apk`
