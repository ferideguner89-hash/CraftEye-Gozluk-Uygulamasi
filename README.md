# CraftEye-Gozluk-Uygulamasi
Bu proje, kişiselleştirilmiş bir gözlük alışverişi deneyimi sunmak amacıyla geliştirilmiş, Kotlin tabanlı bir Android mobil uygulamasıdır. Uygulama; modern e-ticaret özelliklerini, dijital sağlık takibi ve kişiselleştirilmiş öneri algoritmalarıyla birleştirir.
##Kullanılan Teknolojiler ve Mimari

Programlama Dili: Kotlin.

Mimari Yapı: Üç ana Activity ve Navigation Component destekli modüler Fragment yapısı.

Veritabanı & Auth: Firebase Cloud Firestore ve Firebase Authentication.

API Entegrasyonları:

Google Maps API: "Adreslerim" bölümünde konum görselleştirme ve yönetimi.

Döviz API (Frankfurter): Ürünlerin güncel kur verileriyle gerçek zamanlı fiyatlandırılması.

## Veritabanı Yedeği ve Yapılandırma
Firebase'in NoSQL (döküman tabanlı) yapısı gereği, projenin tüm veritabanı bağlantı ayarları ve yapılandırma yedeği projenin ana dizinindeki google-services.json dosyasında yer almaktadır. Bu dosya, uygulamanın bulut servisleriyle senkronize çalışması için gerekli olan teknik konfigürasyonu ve bağlantı yedeğini temsil eder.
 ##Öne Çıkan Özellikler

Dijital Reçete Kaydı: Kullanıcıların göz numaralarını ve astigmat değerlerini tarih bazlı saklayabildiği arşiv sistemi.

Kişiselleştirilmiş Öneri: Yüz tipi ve renk algı testleri ile kullanıcıya en uygun çerçevelerin önerilmesi.

Gelişmiş Yönetici Paneli: Ürün ekleme/güncelleme, stok takibi ve kullanıcı hesap yönetimi (pasifleştirme/silme).

Adres Yönetimi: Harita entegrasyonu ile kolay konum belirleme ve teslimat bilgisi girişi.
