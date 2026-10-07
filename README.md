# Karakter Sayacı (Character Count)

Kullanıcının verdiği bir cümlede, istenen bir karakterin kaç kez geçtiğini bulan Spring Boot uygulaması. Büyük/küçük harf duyarlılığı kullanıcı tarafından seçilebilir.

Aynı iş mantığı (`CharacterCountService`) iki farklı arayüzden kullanılır:

- **REST API:** Tüm bilgiler tek istekte gönderilir.
- **Konsol modu:** Bilgiler sırayla sorulur, hatalı girişte aynı soru tekrar sorulur.

## Teknolojiler

- Java 25
- Spring Boot 4.1.1
- Maven (Maven Wrapper ile gelir, ayrıca kurulum gerekmez)

## Çalıştırma

### REST API modu

```
.\mvnw.cmd spring-boot:run
```

Mac/Linux için: `./mvnw spring-boot:run`

Uygulama `http://localhost:8080` adresinde açılır.

### Konsol modu

Konsol akışı yalnızca `console` profili açıkken çalışır:

```
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=console"
```

> **Türkçe karakter notu (Windows):** PowerShell varsayılan olarak UTF-8 kullanmaz. `ı`, `ü`, `ğ` gibi harflerin doğru okunması için uygulamayı başlatmadan önce aynı terminalde şunları çalıştırın:
>
> ```
> chcp 65001
> [Console]::OutputEncoding = [System.Text.Encoding]::UTF8
> [Console]::InputEncoding = [System.Text.Encoding]::UTF8
> ```
>
> Ya da bunları içeren `calistir-konsol.ps1` dosyasını kullanın: `.\calistir-konsol.ps1`

Örnek akış:

```
Lütfen cümlenin maksimum uzunluğunu giriniz: 50
Lütfen cümleyi giriniz: Merhaba Dünya!
Harf duyarlılığı istiyor musunuz? (Evet/Hayır): Hayır
Lütfen sayılacak karakteri giriniz: a
Girilen cümlede 'a' harfi toplamda 3 defa geçmektedir.
```

## API

### `POST /api/v1/character-count`

**İstek gövdesi**

| Alan            | Tip    | Açıklama                                              |
|-----------------|--------|-------------------------------------------------------|
| `maxLength`     | int    | Cümlenin maksimum uzunluğu (0'dan büyük olmalı)       |
| `sentence`      | String | Analiz edilecek cümle (boş olamaz)                    |
| `caseSensitive` | String | `Evet` veya `Hayır` (büyük/küçük harf fark etmez)     |
| `character`     | String | Sayılacak tek karakter                                |

```json
{
  "maxLength": 50,
  "sentence": "Merhaba Dünya!",
  "caseSensitive": "Hayır",
  "character": "a"
}
```

**Başarılı yanıt (200)**

```json
{
  "count": 3,
  "character": "a",
  "message": "Girilen cümlede 'a' harfi toplamda 3 defa geçmektedir."
}
```

**Hatalı yanıt (400)**

```json
{ "message": "Lütfen geçerli bir cevap giriniz." }
```

### Örnek istek (curl)

Windows PowerShell'de tırnak ve Türkçe karakter sorunlarından kaçınmak için JSON'u dosyadan gönderin:

```
curl.exe -X POST http://localhost:8080/api/v1/character-count -H "Content-Type: application/json; charset=utf-8" --data-binary "@istek.json"
```

## Doğrulama kuralları

Kontroller şu sırayla yapılır ve ilk hatada durur:

| Sıra | Kontrol                                   | Hata mesajı                                  |
|------|-------------------------------------------|----------------------------------------------|
| 1    | `maxLength` 0'dan büyük mü?               | Lütfen geçerli bir değer giriniz. ...        |
| 2    | Cümle boş / sadece boşluk mu?             | Lütfen geçerli bir değer giriniz. ...        |
| 3    | Cümle uzunluğu limiti aşıyor mu?          | Lütfen geçerli bir değer giriniz. ...        |
| 4    | `caseSensitive` Evet/Hayır dışında mı?    | Lütfen geçerli bir cevap giriniz.            |
| 5    | Karakter boş, boşluk ya da 1'den uzun mu? | Geçerli bir karakter giriniz                 |

Tüm hatalar `InvalidInputException` ile fırlatılır ve `GlobalExceptionHandler` tarafından `{ "message": "..." }` formatında, **400** koduyla döndürülür.

## Tasarım kararları ve varsayımlar

- **Konsol akışı → API eşleştirmesi:** Case'deki "tekrar girmesini isteyin" ifadesi API'de "400 + anlamlı hata mesajı" olarak karşılanır; istemci mesajı görüp yeniden istek atar. Konsol modunda ise gerçekten aynı soru tekrar sorulur.
- **`caseSensitive` ve `character` alanları `String`:** `boolean`/`char` kullanılsaydı geçersiz değerler (ör. `"belki"`, `"ab"`, boş değer) Spring tarafından kendi hata formatıyla reddedilir ve case'de istenen mesajlara ulaşılamazdı. Ham değer alınıp doğrulama serviste yapılır.
- **Tek karakter:** `character` alanı tam 1 karakter olmalıdır. Boş, boşluk veya birden fazla karakter `Geçerli bir karakter giriniz` hatası verir. Boşluk karakteri analiz edilmez.
- **Türkçe locale:** Duyarsız modda karşılaştırma `toLowerCase(Locale.forLanguageTag("tr"))` ile yapılır; böylece `I/ı` ve `İ/i` doğru eşleşir.
- **Kontrol sırası:** `maxLength` ve cümle kontrolleri limit kontrolünden önce gelir; geçersiz limit veya `null` cümle ile uzunluk karşılaştırması yapılmaz.
- **DTO'lar `record`:** İstek ve yanıt modelleri değiştirilemez (immutable) ve kısa olsun diye `record` olarak yazıldı.
- **Doğrulama ayrı metotlarda:** `validateMaxLength`, `validateSentence`, `validateCaseSensitive`, `validateCharacter` metotları `public` tutuldu; böylece konsol arayüzü her adımda ilgili kontrolü çağırabilir ve kod tekrar edilmez.

## Proje yapısı

```
com.harates.charactercount
├── CharactercountApplication.java
├── controller/   CharacterCountController
├── service/      CharacterCountService
├── dto/          CharacterCountRequest, CharacterCountResponse
├── exception/    InvalidInputException, GlobalExceptionHandler
└── console/      ConsoleRunner (yalnızca "console" profilinde)
```
