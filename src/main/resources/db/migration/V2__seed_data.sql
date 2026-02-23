-- V2: seed frontend-aligned catalog data

INSERT INTO users (email, password, name, role) VALUES
('admin@example.com','admin123','Administrator','ADMIN'),
('user@example.com','user123','Regular User','USER');

INSERT INTO categories (name, description) VALUES
('Audio','Audio products and accessories'),
('Wearables','Wearable devices and accessories'),
('Gaming','Gaming gear and accessories'),
('Workspace','Workspace and desk essentials'),
('Lifestyle','Lifestyle accessories'),
('Creator','Creator tools and accessories');

INSERT INTO products (name, description, price, stock, category_id, image_url, rating, tag, is_active) VALUES
('Aurora Wireless Headphones','Immersive over-ear headphones with adaptive noise cancelling and 40-hour battery.',129.99,18,1,'https://tse2.mm.bing.net/th/id/OIP.vTVIHODiYbK-_FK4ffZaCgHaIX?pid=Api&P=0&h=220',4.6,'Best Seller',TRUE),
('Nova Smartwatch Pro','A performance smartwatch with precision health metrics and sleek titanium casing.',199.00,25,2,'https://tse4.mm.bing.net/th/id/OIP.cqhmZPvuKixu5Tl6PFQHKgAAAA?pid=Api&P=0&h=220',4.3,'New',TRUE),
('Pulse Gaming Keyboard','Mechanical keyboard with programmable RGB layers and rapid response switches.',89.50,11,3,'https://tse4.mm.bing.net/th/id/OIP.uVvJos_mYmNdoCBEd5y7rwHaES?pid=Api&P=0&h=220',4.7,'Limited',TRUE),
('Orbit Laptop Stand','Adjustable aluminum stand that keeps your setup clean and ergonomic.',39.99,30,4,'https://www.avlfx.com/images/com_hikashop/upload/thumbnails/430x430f/rolling_laptop_stand_-3.jpg',4.4,'Ergonomic',TRUE),
('Pixel Portable Speaker','Compact speaker with punchy bass and water-resistant build.',69.00,20,1,'https://down-ph.img.susercontent.com/file/096a55f1ae5d349477d687d93d562eae',4.1,'Outdoor',TRUE),
('Vertex VR Headset','Next-gen VR headset with ultra-wide field of view and precision tracking.',349.99,6,3,'https://pics.craiyon.com/2023-10-03/344101e950fa483e984eb2e0666ed6ae.webp',4.8,'Pro Gear',TRUE),
('Lumen Desk Lamp','Adaptive desk lamp with warm gradients for late-night focus.',49.00,34,4,'https://stormvintage.nl/wp-content/uploads/2023/04/IMG_2189.jpeg',4.2,'Ambient',TRUE),
('Flux Travel Backpack','Organized backpack with modular compartments and padded tech sleeve.',119.00,14,5,'https://d119zkpqijcapd.cloudfront.net/app/public/uploads/m/2024/03/1-s1zSzSOq36YtjWya9GbGdpQ1QxRfhbBSJRMLcpzS.jpg',4.5,'Traveler',TRUE),
('Echo Noise-Cancelling Earbuds','Pocket-sized earbuds with adaptive ANC and crisp call quality.',89.00,40,1,'https://i.ebayimg.com/images/g/UhcAAOSwLRJmkryi/s-l500.jpg',4.0,'Compact',TRUE),
('Halo RGB Mouse','Precision gaming mouse with adjustable DPI and ambient lighting.',59.99,22,3,'https://www.static-src.com/wcsstore/Indraprastha/images/catalog/full/catalog-image/97/MTA-125791153/no-brand_no-brand_full01.jpg',4.4,'Custom',TRUE),
('Prism Phone Gimbal','Cinematic stabilization for mobile creators with AI tracking.',149.00,9,6,'https://5.imimg.com/data5/ECOM/Default/2022/7/AP/LU/TA/142053378/prism-b23a99f3-f877-4c1e-b062-619733772627-1000x1000.jpg',4.5,'Creator',TRUE),
('Nimbus Desk Mat','Premium microfiber mat to level up your workspace and control.',29.00,50,4,'https://tse2.mm.bing.net/th/id/OIP._lG4naO7aICRHqC-J6FRoAHaHa?pid=Api&P=0&h=220',4.1,'Minimal',TRUE);
