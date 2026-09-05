# Nobitex Trader Secure Final v3

این بسته نسخه‌ی امن‌تر و یکپارچه‌ی پروژه است.

**معماری:** Android APK ← HTTPS → FastAPI روی VPS ←→ API رسمی Nobitex

- Spot only؛ بدون Margin/Leverage
- Paper به صورت پیش‌فرض
- کلید Nobitex فقط روی VPS
- بدون endpoint برداشت
- سرمایه قابل تخصیص از موجودی آزاد ریالی
- ذخیره‌ی وضعیت و معاملات در SQLite
- Emergency Stop و Daily Loss Stop
- جلوگیری از ادامه‌ی معامله پس از Restart با وضعیت ذخیره‌شده
- Build خودکار APK در GitHub Actions

### نکته‌ی مهم
این پروژه تضمین سود نمی‌دهد. قبل از Live با سرمایه کم تست شود. API Key باید فقط مجوز معامله داشته باشد و **برداشت نداشته باشد**.

مستندات رسمی Nobitex: https://apidocs.nobitex.ir/
