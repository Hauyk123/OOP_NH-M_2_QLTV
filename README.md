🚀 Hướng Dẫn Cài Đặt & Khởi Chạy (Installation & Run)

Dự án được xây dựng bằng Java Swing và sử dụng Maven để quản lý thư viện. Bạn có thể dễ dàng thiết lập và chạy dự án theo các bước sau:

1. Yêu cầu hệ thống (Prerequisites)

Để chạy được ứng dụng, máy tính của bạn cần cài đặt sẵn:

Java JDK 21 (hoặc phiên bản JDK từ 17 trở lên).

Apache Maven 3.x (nếu chạy qua Terminal).

IDE khuyên dùng: IntelliJ IDEA, Eclipse, hoặc Visual Studio Code.

2. Các bước cài đặt (Setup)

Bước 1: Tải mã nguồn về máy
Bạn có thể Clone dự án qua Git hoặc tải trực tiếp file ZIP:

git clone https://github.com/your-username/your-repo-name.git


(Lưu ý: Nhớ thay your-username/your-repo-name bằng link github thật của bạn)

Bước 2: Mở dự án trong IDE (Khuyên dùng IntelliJ IDEA)

Mở IntelliJ IDEA ➔ Chọn Open ➔ Trỏ tới thư mục chứa dự án vừa tải.

Đợi một chút để IDE nhận diện cấu trúc dự án.

Ở góc phải màn hình, mở tab Maven ➔ Bấm biểu tượng Reload All Maven Projects (Hình hai mũi tên xoay tròn) để hệ thống tự động tải về các thư viện cần thiết (Google Books API, ZXing, v.v.).

3. Khởi chạy ứng dụng (Run)

Cách 1: Chạy trực tiếp trên giao diện IDE (Dễ nhất)

Mở file khởi chạy chính theo đường dẫn:
src/main/java/org/example/Main.java

Click chuột phải vào khoảng trống trong code ➔ Chọn Run Main.java

Cách 2: Chạy bằng dòng lệnh (Terminal)
Mở Terminal tại thư mục gốc của dự án và chạy các lệnh sau:

# Biên dịch dự án
mvn clean install

# Khởi chạy ứng dụng bằng Maven plugin (nếu có cấu hình trong pom.xml)
mvn exec:java -Dexec.mainClass="org.example.Main.java"


🔑 Tài khoản Test nghiệm thu (Demo Accounts)

Sau khi giao diện Đăng nhập hiện lên, bạn có thể sử dụng các tài khoản có sẵn trong Database (.txt) sau đây để trải nghiệm:

1. Tài khoản Quản trị / Thủ thư (Admin Role):

Username: admin1

Password: admin1

Quyền hạn: Toàn quyền hệ thống (Quản lý sách, Thêm/Sửa/Xóa, Dashboard Thống kê, Quản lý thành viên).

2. Tài khoản Người dùng (User Role):

Username: test1

Password: 123456

Quyền hạn: Xem danh mục sách, tự mượn sách, theo dõi sách đã mượn (Đang mượn/Đã trả) và xem hồ sơ cá nhân.

⚠️ Lưu ý quan trọng (Troubleshooting)

Lỗi Font chữ tiếng Việt: Dự án đọc/ghi file .txt có chứa tiếng Việt. Hãy đảm bảo File Encoding trong IDE của bạn được cài đặt là UTF-8 (Vào File -> Settings -> Editor -> File Encodings -> Chọn UTF-8 cho toàn bộ).

Lỗi không tìm thấy dữ liệu: Ứng dụng đọc file TXT từ đường dẫn tương đối (src/main/java/org/example/data/). Vui lòng đảm bảo Working Directory khi chạy ứng dụng là thư mục gốc (Root) của dự án.
