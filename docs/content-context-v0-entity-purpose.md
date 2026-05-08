# Content Context V0 — Vai trò của từng Entity trong DeutschHub

Tài liệu này trả lời câu hỏi: ở **Version 0**, với các entity hiện có (`Article`, `Content_Page`, `Category`, `Topic`, `Article_Version`, `Media`), mỗi entity làm được gì cho dự án và vì sao cần nó.

## 1) `Article`

### Làm được gì
- Là đơn vị nội dung chính để dạy/học (bài viết ngữ pháp, từ vựng, kỹ năng đọc...).
- Quản lý vòng đời nội dung: draft → review (nếu có) → publish → archive.
- Chứa metadata phục vụ hiển thị và SEO cơ bản: title, slug, summary, thumbnail, publishAt.
- Liên kết với `Category`, `Topic`, `Media` và dữ liệu version.

### Vì sao cần
- Đây là “product unit” mà learner thực sự đọc và hệ thống thực sự phân phối.
- Nếu không có `Article`, không có đối tượng rõ ràng để đo engagement, tìm kiếm, phân loại, hay kiểm soát chất lượng.

### Giá trị cho V0
- Cho phép team ship luồng tối thiểu: tạo draft → sửa → publish → hiển thị trên page.

---

## 2) `Content_Page`

### Làm được gì
- Đóng vai trò “container hiển thị”: gom nhiều `Article` theo layout (home section, landing page, chuyên đề).
- Điều phối thứ tự hiển thị (ordering) của article.
- Bật/tắt trạng thái publish của chính trang (để staging nội dung theo batch).

### Vì sao cần
- Nếu chỉ có `Article` riêng lẻ thì chưa có trải nghiệm editorial (không có trang curated nội dung).
- `Content_Page` giúp tách concern “nội dung đúng” khỏi “nội dung được trưng bày như thế nào”.

### Giá trị cho V0
- Có thể nhanh chóng tạo trang chủ học tập, trang chuyên mục, trang chiến dịch mà không phải code cứng.

---

## 3) `Category`

### Làm được gì
- Cung cấp phân loại business-level (ví dụ: Grammar, Vocabulary, Exam Prep).
- Là trục filter/facet cho list page và search.
- Có thể hỗ trợ cây cha-con đơn giản để điều hướng.

### Vì sao cần
- Người học cần tìm nhanh nội dung theo “mảng học”; đội vận hành cần thống kê theo mảng.
- Giúp content governance rõ ràng hơn (mỗi bài thuộc phạm vi chuyên môn nào).

### Giá trị cho V0
- Đủ để xây menu điều hướng, filter cơ bản, và báo cáo số lượng bài theo chuyên mục.

---

## 4) `Topic`

### Làm được gì
- Tag ngữ nghĩa linh hoạt hơn `Category` (ví dụ: Akkusativ, Trennbare Verben, B1 Schreiben).
- Hỗ trợ recommendation đơn giản kiểu “bài liên quan theo topic”.
- Hỗ trợ tìm kiếm theo intent học cụ thể.

### Vì sao cần
- `Category` thường thô và ổn định; `Topic` linh hoạt và chi tiết hơn để cá nhân hóa trải nghiệm học.
- Hữu ích cho SEO long-tail và internal linking.

### Giá trị cho V0
- Chỉ với tag/topic, đã có thể tăng khám phá nội dung và giữ chân người học tốt hơn.

---

## 5) `Article_Version`

### Làm được gì
- Lưu snapshot nội dung mỗi lần chỉnh sửa quan trọng.
- Cho phép publish một version cụ thể, rollback khi cần.
- Tạo audit trail “ai sửa gì, khi nào” ở mức nội dung.

### Vì sao cần
- Nội dung học thuật cần tính đúng/sai cao; chỉnh sửa nhầm có thể ảnh hưởng nhiều người học.
- Versioning giảm rủi ro overwrite, giúp team biên tập làm việc an toàn hơn.

### Giá trị cho V0
- Dù chỉ cần version cơ bản, vẫn đủ để tránh mất dữ liệu và hỗ trợ rollback nhanh khi có lỗi nội dung.

---

## 6) `Media`

### Làm được gì
- Đại diện tài nguyên media dùng lại: ảnh minh họa, audio phát âm, file đính kèm.
- Quản lý metadata file: URL/key, mimeType, size, checksum (nếu có).
- Theo dõi trạng thái sử dụng (đang được article nào tham chiếu).

### Vì sao cần
- Tách media khỏi article giúp tái sử dụng asset, giảm trùng lặp, và kiểm soát vòng đời file.
- Cần thiết cho performance (CDN), quyền truy cập, và quản lý chi phí lưu trữ.

### Giá trị cho V0
- Có nền tảng để triển khai nội dung đa phương tiện ngay từ đầu thay vì “hard-code link file”.

---

## Nhìn tổng thể: 6 entity này ghép lại tạo ra năng lực gì cho DeutschHub V0?

- **Authoring**: tạo/sửa/publish nội dung có kiểm soát (`Article` + `Article_Version`).
- **Organization**: tổ chức tri thức theo cấu trúc rõ ràng (`Category` + `Topic`).
- **Presentation**: đóng gói trải nghiệm hiển thị cho người học (`Content_Page`).
- **Rich learning assets**: đưa audio/ảnh/tài nguyên vào bài học an toàn (`Media`).

=> Đây là một “vertical slice” đủ tốt để launch Content Context bản đầu, trước khi mở rộng các capability nâng cao.

---

## Gợi ý rất ngắn để hoàn thiện V0 (chỉ bổ sung nếu cần)

Không mở rộng quá nhiều, nhưng nếu cần hoàn thiện V0 ổn hơn, nên cân nhắc tối thiểu:

1. **`Author` (hoặc dùng tạm `createdBy/updatedBy`)**
   - Để biết ownership nội dung và phục vụ audit vận hành.
2. **`SlugPolicy` (domain service/policy)**
   - Đảm bảo slug unique + chuẩn hóa slug nhất quán.
3. **`PublishWindow` (VO nhỏ)**
   - Chuẩn hóa rule không schedule publish ở quá khứ.

> Nếu team muốn giữ cực gọn cho V0, có thể chưa tách thành entity mới mà biểu diễn bằng field + policy/service.
