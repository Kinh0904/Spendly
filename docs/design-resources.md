# Tài nguyên giao diện Spendly

Nguồn: `Spendly-Figma/code.js` ở thư mục bàn giao, SHA-256
`0EDE6BF581B43D2E76B44DEFA07238D56F0D9F6C2982A8022DED82C480A38E50`.
`Test/code.ts` là plugin mẫu tạo hình chữ nhật, không phải nguồn thiết kế.

## Cách sử dụng

| Thư mục/file dưới `app/src/main/res` | Nội dung |
| --- | --- |
| `values/spendly_palette.xml` | 40 màu gốc, 20 màu cho mỗi theme |
| `values/spendly_colors.xml`, `values-night/spendly_colors.xml` | 20 tên màu semantic dùng chung, tự chọn theo chế độ sáng/tối |
| `values/spendly_dimens.xml` | 16 token khoảng cách/bo góc, kích thước chữ và thành phần |
| `values/spendly_typography.xml` | 8 TextAppearance: Display, Heading, Title, Subtitle, Body, Label, Caption, Micro |
| `values/spendly_strings.xml` | Nhãn tiếng Việt; dữ liệu động dùng tham số, không lưu tài khoản/số dư mẫu |
| `drawable/ic_*.xml` | 25 icon vector lấy nguyên đường nét từ plugin |
| `drawable/bg_spendly_*.xml` | Nền card, notice, dialog, avatar, nút, ô nhập và chip |
| `color/spendly_*.xml` | Màu chữ nút theo trạng thái, chip và mục điều hướng |
| `values/themes.xml`, `values*/spendly_bools.xml` | Theme Material 3 và màu thanh hệ thống |

Dùng `@color/spendly_primary`, không dùng trực tiếp màu có hậu tố `_light` hoặc `_dark`.
Tên camelCase của Figma được chuyển thành snake_case: `primaryPressed` →
`spendly_primary_pressed`, `onPrimary` → `spendly_on_primary`.
Các kích thước Figma được chuyển sang dp; cỡ chữ và chiều cao dòng chuyển sang sp.

Ví dụ trong layout:

```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/spendly_monthly_budget"
    android:textAppearance="@style/TextAppearance.Spendly.Subtitle" />
```

Nền nút hỗ trợ disabled/pressed/default. Màu chữ tương ứng nằm trong `color/`.
Nền ô nhập hỗ trợ disabled/focused/default; khi kiểm tra dữ liệu báo lỗi, gán
`bg_spendly_input_error` riêng (Android drawable không có `state_error`).
Chip hỗ trợ cả checked và selected. Với MaterialButton/TextInputLayout/Chip,
hãy cấu hình tint/shape của widget thay vì thay background do Material quản lý.
Đặt vùng chạm tối thiểu `@dimen/spendly_touch_target_min` khi dùng icon/chip nhỏ.

Icon mặc định theo màu xanh của **hàm dựng icon trong plugin**, không theo các
màu xanh lá cũ lưu trong ICONS. Ví và avatar giữ nhiều lớp màu; tránh tint toàn bộ
hai hình này. Google/Facebook cũng giữ cách tô xanh đơn sắc của plugin hiện tại.
Icon có kích thước mặc định 24dp đúng component nguồn, kể cả ví và avatar;
viewport giữ nguyên để không biến dạng khi tăng kích thước trong ImageView.

Roboto được ánh xạ sang `sans-serif`/`sans-serif-medium` và bold của Android.
Nguồn không có TTF/OTF nên không tạo thư mục `font/` hay font tải qua mạng.
Trên thiết bị tùy biến font hệ thống, kiểu chữ thực tế có thể khác Roboto gốc.

Ảnh preview/tổng quan không phải asset trong UI nên không đóng gói vào APK.
Launcher mặc định được giữ nguyên vì plugin cũng bỏ qua hai icon launcher.
Tài nguyên được chuẩn bị cho việc dựng màn hình. Các màn hình đăng nhập/trang chủ
đã có trên `develop` được giữ lại khi hợp nhất; chưa chuyển toàn bộ layout cũ
sang thiết kế Figma. `Theme.Spendly` là theme chính; `Theme.Spending` là tên tương
thích với mã cũ. Giữ `Widget.Spendly.CardView` cho các layout hiện có.
Chế độ tối dùng cùng base theme với màu override.
