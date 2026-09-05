# Nobitex Trader — Android + Cloud Build

این پروژه برای ساخت APK اندروید با GitHub Actions آماده شده است.
نسخه فعلی برای ایمنی، فقط کنترل و نمایش وضعیت ربات را انجام می‌دهد و اجرای معاملات واقعی
باید پس از تست و تنظیم سرور انجام شود.

## ساخت APK
1. کل محتوای این پوشه را در ریشه repository با نام `nobitex-trader` قرار دهید.
2. در GitHub به Actions بروید.
3. workflow با نام `Build Android APK` را اجرا کنید.
4. پس از پایان موفق، از بخش Artifacts فایل `nobitex-trader-debug-apk` را دانلود کنید.

## امنیت
API Key نوبیتکس را داخل repository، APK یا چت قرار ندهید.
برای معاملات واقعی، کلید باید فقط روی سرور و بدون مجوز برداشت نگهداری شود.
