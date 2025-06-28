INSERT INTO public.product (
    productid, barcode, category, description, dimensions, imageurl, eligible, price, quantity, title, value, warehouse_entry_date, weight)
     VALUES
-- Books (Giá VNĐ)
('B001', '9781234567890', 'book', 'Mystery novel in London', '20x13x3 cm', 'https://drive.google.com/file/d/1MMD7eQ1V8tJhaE91WekgA94Kvs7WMZjn/view?usp=drive_link', true, 159000, 100, 'The London Enigma', 4.5, DATE '2025-06-10', 0.5),
('B002', '9781111111111', 'book', 'Historical fiction in Rome', '21x14x3 cm', 'https://drive.google.com/file/d/1pLEcPtben-GfDP_lv5vQrcmnD5E6IsO4/view?usp=drive_link', false, 125000, 80, 'Echoes of Empire', 4.1, DATE '2025-01-02', 0.6),
('B003', '9782222222222', 'book', 'Science fiction odyssey', '19x12x2.5 cm', 'https://drive.google.com/file/d/1aeFH5UumTsfW4Kwn769mDyy816cthlUg/view?usp=sharing', true, 180000, 60, 'The Last Horizon', 4.8, DATE '2025-02-20', 0.45),
('B004', '9783333333333', 'book', 'Romantic drama', '18x11x2 cm', 'https://drive.google.com/file/d/1cvoN3Yst0H1YwAXd78Fb_Lxb0pschHPE/view?usp=drive_link', false, 110000, 120, 'Hearts in Winter', 3.9, DATE '2024-12-15', 0.4),
('B005', '9784444444444', 'book', 'Biography of a tech leader', '22x15x4 cm', 'https://example.com/book5.jpg', true, 220000, 50, 'Code & Legacy', 4.6, DATE '2024-11-01', 0.7),
('B006', '9785555555555', 'book', 'Political thriller', '20x13x2.8 cm', 'https://example.com/book6.jpg', true, 140000, 70, 'The Third Vote', 4.2, DATE '2025-03-01', 0.55),
('B007', '9786666666666', 'book', 'Children’s fantasy', '21x14x3 cm', 'https://example.com/book7.jpg', false, 95000, 90, 'Magical Tales', 4.0, DATE '2024-10-10', 0.3),
('B008', '9787777777777', 'book', 'Travel memoir', '23x16x2.5 cm', 'https://example.com/book8.jpg', false, 137500, 65, 'Footsteps of the World', 4.3, DATE '2025-05-05', 0.6),
('B009', '9788888888888', 'book', 'Self-help and productivity', '20x13x2 cm', 'https://example.com/book9.jpg', true, 162500, 110, 'Mind in Motion', 4.7, DATE '2025-06-01', 0.45),
('B010', '9789999999999', 'book', 'Modern poetry collection', '19x12x1.8 cm', 'https://example.com/book10.jpg', false, 89900, 150, 'Words Unspoken', 4.4, DATE '2024-09-21', 0.35),

-- CDs
('C001', '0123456789123', 'cd', 'Classic rock album', '14x12x1 cm', 'https://example.com/cd1.jpg', false, 99000, 50, 'Retro Rock Vol.1', 4.0, DATE '2025-05-15', 0.1),
('C002', '0123456789124', 'cd', 'Jazz fusion live recording', '14x12x1 cm', 'https://example.com/cd2.jpg', true, 119900, 40, 'Live at the Blue Note', 4.3, DATE '2025-01-12', 0.1),
('C003', '0123456789125', 'cd', 'Acoustic folk', '14x12x1 cm', 'https://example.com/cd3.jpg', false, 84900, 70, 'Strings of Silence', 3.8, DATE '2025-02-10', 0.09),
('C004', '0123456789126', 'cd', 'Electronic ambient', '14x12x1 cm', 'https://example.com/cd4.jpg', true, 100000, 30, 'Neon Dreams', 4.2, DATE '2025-03-18', 0.08),
('C005', '0123456789127', 'cd', 'Pop hits compilation', '14x12x1 cm', 'https://example.com/cd5.jpg', false, 120000, 100, 'PopStars Mix 2024', 4.6, DATE '2024-12-01', 0.1),
('C006', '0123456789128', 'cd', 'Heavy metal classic', '14x12x1 cm', 'https://example.com/cd6.jpg', true, 134900, 20, 'Iron Pulse', 4.7, DATE '2025-04-12', 0.1),
('C007', '0123456789129', 'cd', 'K-Pop mini album', '14x12x1 cm', 'https://example.com/cd7.jpg', true, 150000, 90, 'Galaxy Bloom', 4.9, DATE '2025-06-01', 0.1),
('C008', '0123456789130', 'cd', 'Instrumental soundtrack', '14x12x1 cm', 'https://example.com/cd8.jpg', false, 95000, 60, 'Beyond the Horizon OST', 4.1, DATE '2025-05-20', 0.09),
('C009', '0123456789131', 'cd', 'Classical piano', '14x12x1 cm', 'https://example.com/cd9.jpg', true, 109900, 35, 'Piano Solitude', 4.5, DATE '2025-06-05', 0.1),
('C010', '0123456789132', 'cd', 'Latin dance mix', '14x12x1 cm', 'https://example.com/cd10.jpg', false, 87500, 45, 'Ritmo Caliente', 4.0, DATE '2025-04-01', 0.1),

-- DVDs
('D001', '3210987654321', 'dvd', 'Sci-fi thriller movie', '19x13.5x1.5 cm', 'https://example.com/dvd1.jpg', true, 124900, 30, 'Stars Beyond', 4.7, DATE '2025-04-01', 0.15),
('D002', '3210987654322', 'dvd', 'Romantic comedy', '19x13.5x1.5 cm', 'https://example.com/dvd2.jpg', false, 109900, 40, 'Love & Latte', 4.0, DATE '2025-03-15', 0.15),
('D003', '3210987654323', 'dvd', 'Action blockbuster', '19x13.5x1.5 cm', 'https://example.com/dvd3.jpg', true, 145000, 25, 'City on Fire', 4.6, DATE '2025-01-10', 0.16),
('D004', '3210987654324', 'dvd', 'Horror anthology', '19x13.5x1.5 cm', 'https://example.com/dvd4.jpg', true, 99900, 20, 'Whispers in the Dark', 3.9, DATE '2025-02-22', 0.14),
('D005', '3210987654325', 'dvd', 'Documentary film', '19x13.5x1.5 cm', 'https://example.com/dvd5.jpg', false, 80000, 30, 'Earth Pulse', 4.2, DATE '2024-12-05', 0.13),
('D006', '3210987654326', 'dvd', 'Drama feature', '19x13.5x1.5 cm', 'https://example.com/dvd6.jpg', false, 112500, 28, 'Fading Light', 4.3, DATE '2025-05-10', 0.14),
('D007', '3210987654327', 'dvd', 'Animated kids film', '19x13.5x1.5 cm', 'https://example.com/dvd7.jpg', true, 130000, 60, 'Dragonfly Adventures', 4.8, DATE '2025-06-01', 0.15),
('D008', '3210987654328', 'dvd', 'Fantasy epic', '19x13.5x1.5 cm', 'https://example.com/dvd8.jpg', true, 167500, 22, 'The Shattered Crown', 4.5, DATE '2025-05-21', 0.18),
('D009', '3210987654329', 'dvd', 'Thriller', '19x13.5x1.5 cm', 'https://example.com/dvd9.jpg', false, 120000, 50, 'Deep Vault', 4.1, DATE '2025-04-18', 0.15),
('D010', '3210987654330', 'dvd', 'Historical war film', '19x13.5x1.5 cm', 'https://example.com/dvd10.jpg', true, 139000, 26, 'Legacy of Steel', 4.4, DATE '2025-03-29', 0.16);

INSERT INTO public.book (productid, authors, cover_type, genre, language, number_of_pages, pub_date, publisher) VALUES
('B001', 'Arthur Doyle', 'Hardcover', 'Mystery', 'English', 350, DATE '2024-11-01', 'Mystery House Publishing'),
('B002', 'Lucia Roman', 'Paperback', 'Historical', 'English', 420, DATE '2023-10-10', 'Golden Scrolls'),
('B003', 'Mark A. Liu', 'Paperback', 'Sci-Fi', 'English', 390, DATE '2025-02-05', 'FuturePress'),
('B004', 'Elena Sparks', 'Hardcover', 'Romance', 'English', 310, DATE '2022-08-18', 'Hearts & Words'),
('B005', 'Jonathan Tech', 'Hardcover', 'Biography', 'English', 500, DATE '2021-12-01', 'Bright Path'),
('B006', 'Tom L. Wright', 'Paperback', 'Thriller', 'English', 330, DATE '2024-06-15', 'Pulse Books'),
('B007', 'Sally Dreamer', 'Hardcover', 'Fantasy', 'English', 280, DATE '2023-05-20', 'Tiny Giants'),
('B008', 'Greg Walker', 'Paperback', 'Travel', 'English', 260, DATE '2022-09-30', 'Nomad Words'),
('B009', 'Dina Lang', 'Paperback', 'Self-help', 'English', 290, DATE '2025-01-01', 'FocusHouse'),
('B010', 'Amira Snow', 'Hardcover', 'Poetry', 'English', 180, DATE '2023-03-11', 'LyricPress');

INSERT INTO public.cd (productid, artist, music_type, record_label, release_date, tracklist) VALUES
('C001', 'The Retro Kings', 'Rock', 'OldWave Records', DATE '1995-08-15', 'Intro, Thunder Road, Night Drive'),
('C002', 'Jazz Fusionists', 'Jazz', 'Blue Note', DATE '2020-07-22', 'Start, Flow, End'),
('C003', 'Folkway', 'Folk', 'Indie Tree', DATE '2019-11-10', 'Windsong, Riverbed, Home'),
('C004', 'DJ Nova', 'Electronic', 'Waveform Inc.', DATE '2021-06-01', 'Neon 1, Pulse, Drift'),
('C005', 'Various Artists', 'Pop', 'Universal Music', DATE '2024-01-15', 'Hit1, Hit2, Hit3'),
('C006', 'MetalForge', 'Metal', 'SteelSounds', DATE '2000-05-05', 'Burn, Steel, Glory'),
('C007', 'Starwave', 'K-Pop', 'Galaxy Entertainment', DATE '2025-05-01', 'Bloom, Dance, Sky'),
('C008', 'Hans V', 'Instrumental', 'SoundFactory', DATE '2017-08-14', 'Theme1, Theme2, Finale'),
('C009', 'A. Legrand', 'Classical', 'ClassicLine', DATE '2015-02-12', 'Sonata I, II, III'),
('C010', 'DJ Latino', 'Latin', 'Fiesta Mix', DATE '2023-12-01', 'Rumba, Salsa, Reggaeton');

INSERT INTO public.dvd (productid,director, disc_type, genre, language, release_date, runtime, studio, subtitle) VALUES
('D001', 'Jane Stellar', 'Blu-ray', 'Sci-Fi', 'English', DATE '2023-12-20', '2h 10m', 'Nova Studios', 'English, Spanish'),
('D002', 'Robert Lin', 'DVD', 'Romance', 'English', DATE '2022-02-14', '1h 45m', 'Love Reel', 'English'),
('D003', 'Miko Tanaka', 'Blu-ray', 'Action', 'Japanese', DATE '2023-04-05', '2h 00m', 'Tokyo Lights', 'English, Japanese'),
('D004', 'Sarah Bloom', 'DVD', 'Horror', 'English', DATE '2021-10-31', '1h 50m', 'NightFrame', 'English'),
('D005', 'David Chen', 'DVD', 'Documentary', 'English', DATE '2020-01-01', '1h 20m', 'EarthFilms', 'English'),
('D006', 'Anna Ramires', 'Blu-ray', 'Drama', 'Spanish', DATE '2022-08-22', '2h 05m', 'CineArte', 'Spanish, English'),
('D007', 'Lee Wong', 'DVD', 'Animation', 'English', DATE '2024-03-03', '1h 30m', 'Toonville', 'English, French'),
('D008', 'Oscar Wilde', 'Blu-ray', 'Fantasy', 'English', DATE '2023-07-15', '2h 25m', 'Epic Studios', 'English, German'),
('D009', 'Zoe Clarke', 'DVD', 'Thriller', 'English', DATE '2022-11-11', '1h 55m', 'EdgeFilm', 'English'),
('D010', 'Hugo Steiner', 'Blu-ray', 'War', 'German', DATE '2021-05-08', '2h 15m', 'EuropaFilm', 'German, English');

