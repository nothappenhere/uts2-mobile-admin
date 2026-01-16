# AgroOrder Admin App

Aplikasi mobile Android untuk **Admin/Petani** pada sistem pemesanan hasil tani "AgroOrder". Aplikasi ini dibangun dengan **Kotlin** dan **Android Studio** menggunakan arsitektur View System (XML Layout + Fragments).

## Deskripsi Aplikasi
AgroOrder adalah sistem pemesanan hasil tani berbasis client-admin. Aplikasi Admin digunakan oleh admin/petani untuk:
- Melihat dan meng-approve registrasi client (restoran/rumah makan).
- Melihat daftar order dari client.
- Melihat detail perhitungan order (subtotal, pajak, ongkir).
- Mengubah status order (Pending → Approved → Shipped → Delivered).
- Menampilkan informasi cuaca lokasi petani dari API publik (OpenWeatherMap).

## Fitur Utama
- Login Admin
- Approval registrasi client
- Manajemen order (lihat detail + update status)
- Integrasi API cuaca publik (OpenWeatherMap)
- Halaman About dengan informasi API publik yang digunakan

## Teknologi yang Digunakan
- **Bahasa**: Kotlin
- **IDE**: Android Studio
- **Arsitektur**: MVVM sederhana + Coroutines + Lifecycle
- **Networking**: Retrofit 2 + Gson
- **Database/Backend**: Node.js + Express + PostgreSQL (terpisah)
- **Authentication**: JWT Token
- **UI**: Material Design

## Cara Menjalankan
1. Clone repository ini.
2. Pastikan backend Node.js berjalan di `http://IP_LOKAL:3000` (ganti di `RetrofitClient.kt`).
3. Allow cleartext HTTP di `AndroidManifest.xml` atau gunakan network security config.
4. Build dan jalankan di emulator/real device.

## API Publik yang Digunakan
- **OpenWeatherMap API** (prakiraan cuaca)  
  Website: [https://openweathermap.org](https://openweathermap.org) 

## Pengembang
- 152022166 - Muhammad Rizky Akbar
- 152022142 - Gumiwang Maysa Nusi
- 152022137 - Baraja Barsya P.
- 152022169 - Erick Erlangga Putra W.
- 152022144 - Luthfiansyah Putra Dean F.
