# Content Context (Domain Model) – Chốt Aggregate vs Entity hỗ trợ

## Mục tiêu
Xác định ranh giới aggregate trong **Domain/Model** để:
- Khóa invariants đúng chỗ.
- Tránh aggregate quá to.
- Dễ triển khai repository + use case sau này.

## Kết luận nhanh

### Aggregate Roots đề xuất
1. **Article** (root chính)
2. **ContentPage** (root độc lập)
3. **Category** (root độc lập)
4. **Topic** (root độc lập)
5. **Media** (root độc lập)

### Không nên là root riêng (giai đoạn đầu)
- **ArticleVersion**: là entity con bên trong `Article` aggregate để giữ invariant versioning tại chỗ.
- **ContentBlock / SectionMetadata**: entity/value object con của `Article`.

---

## 1) Aggregate: `Article`

### Vì sao là root
`Article` là nơi tập trung phần lớn business invariants biên tập + publish + SEO.

### Bên trong aggregate
- `ArticleVersion` (Entity)
- `ContentBlock` (Entity/VO tùy mức phức tạp)
- `SectionMetadata` (VO)
- `PublishSchedule` (VO)
- `Slug` (VO)

### Invariants đặt ở Article
- Không publish nếu chưa có nội dung.
- Không publish nếu chưa có category.
- Không sửa **version đã publish** (muốn sửa phải tạo version mới).
- Chỉ 1 version published tại một thời điểm.
- Slug phải unique (kiểm tra uniqueness ở domain service/repository port).
- Không schedule publish ở quá khứ.
- Không duplicate topic trong cùng article.

### Method nên có
- `createDraft()`
- `updateContent()` (chỉ tác động draft version)
- `createNewVersionFromCurrent()`
- `publishNow()` / `publishVersion(versionId)`
- `schedulePublish(at)`
- `archive()`
- `assignCategory(categoryId)`
- `assignTopic(topicId)` / `removeTopic(topicId)`
- `attachMedia(mediaId)` / `detachMedia(mediaId)`

---

## 2) Aggregate: `ContentPage`

### Vì sao là root
`ContentPage` quản lý curation/layout của nhiều article để hiển thị FE (About, Why choose us, ...).

### Bên trong aggregate
- `PageSlot` / `PageItem` (entity con, chứa `articleId`, vị trí, rule hiển thị)
- `PageLayout` (VO)

### Invariants
- Không hiển thị article chưa publish.
- Không duplicate article trong cùng page (nếu business yêu cầu).
- Publish page cần có ít nhất 1 content item hợp lệ.

### Method
- `addArticle(articleId, slot)`
- `removeArticle(articleId)`
- `reorderItem(...)`
- `publishPage()`
- `unpublishPage()`

> Lưu ý: rule “article phải published” nên check qua domain service/query policy khi add/publish.

---

## 3) Aggregate: `Category`

### Vì sao là root
`Category` có lifecycle riêng, có thể dùng chung cho nhiều article.

### Bên trong aggregate
- `CategoryName` (VO)
- `ParentCategoryId` (nullable, VO)

### Invariants
- Category name unique.
- Không tạo vòng lặp parent-child.
- Không cho delete nếu còn article tham chiếu.

### Method
- `createCategory()`
- `rename()`
- `changeParent()`
- `delete()` (hoặc `deactivate()`)

---

## 4) Aggregate: `Topic`

### Vì sao là root
`Topic` là taxonomy độc lập, tái sử dụng nhiều nơi.

### Invariants
- Topic name unique toàn hệ thống.
- Duplicate topic trong 1 article được chặn tại `Article` aggregate.

### Method
- `createTopic()`
- `rename()`
- `deprecate()`

---

## 5) Aggregate: `Media`

### Vì sao là root
`Media` có lifecycle storage/usage riêng (upload, attach, detach, delete safety).

### Invariants
- File phải tồn tại trước khi attach.
- URL/path hợp lệ.
- Không xóa media nếu còn reference.

### Method
- `registerUploadedFile()`
- `markAttached(articleId)`
- `markDetached(articleId)`
- `delete()` (chỉ khi reference count = 0)

---

## Ranh giới cross-aggregate (quan trọng)

Các rule sau **không nên** enforce bằng cách load full aggregate chéo:
- `Article` publish cần `Category` tồn tại.
- `ContentPage` chỉ nhận article đã published.
- `Media` phải tồn tại khi attach vào article.

Cách làm đúng:
- Dùng **domain service / policy service** + repository port để kiểm tra existence/state.
- Aggregate chỉ giữ invariant nội bộ + reference id.

---

## Mapping nhanh theo bảng bạn đưa

- **Article**: root ✅
- **Content_Page**: root ✅
- **Category**: root ✅
- **Topic**: root ✅
- **Article_Version**: entity con của Article ✅
- **Media**: root ✅

---

## Đề xuất package (domain/model)

- `domain/content/model/aggregate/Article.java`
- `domain/content/model/aggregate/ContentPage.java`
- `domain/content/model/aggregate/Category.java`
- `domain/content/model/aggregate/Topic.java`
- `domain/content/model/aggregate/Media.java`
- `domain/content/model/entity/ArticleVersion.java`
- `domain/content/model/entity/ContentBlock.java`
- `domain/content/model/valueobject/*`

