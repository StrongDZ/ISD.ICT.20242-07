import React from "react";
import {
  Card,
  CardContent,
  Typography,
  Divider,
  Box,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Avatar,
} from "@mui/material";

/**
 * Component để hiển thị tóm tắt đơn hàng, bao gồm danh sách sản phẩm và chi tiết các loại phí.
 *
 * @param {object} order - Đối tượng đơn hàng đã đặt (nếu có).
 * @param {array} items - Danh sách các sản phẩm trong giỏ hàng.
 * @param {object} shippingFees - Đối tượng chứa phí vận chuyển từ API ({ regularShippingFee, rushShippingFee }).
 * @param {object} deliveryInfo - Thông tin giao hàng.
 * @param {boolean} showTitle - Có hiển thị tiêu đề "Tóm Tắt Đơn Hàng" hay không.
 * @param {boolean} compact - Chế độ hiển thị nhỏ gọn.
 */
const OrderSummary = ({
  order,
  items = [],
  shippingFees = { regularShippingFee: 0, rushShippingFee: 0 },
  deliveryInfo = null,
  showTitle = true,
  compact = false,
}) => {
  const formatPrice = (price) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  };

  // --- Tính toán các giá trị ---

  // 1. Tính tạm tính (subtotal) từ danh sách sản phẩm
  const calculateSubtotal = () => {
    return items.reduce(
      (total, item) => total + (item.productDTO?.price || 0) * item.quantity,
      0
    );
  };

  // Ưu tiên subtotal từ `order` nếu có, nếu không thì tính toán lại
  const subtotal = order?.subtotal ?? calculateSubtotal();

  // 2. Lấy phí vận chuyển từ props, ưu tiên `order`
  // Phí này được truyền vào từ kết quả gọi API /calculate-shipping-fees
  const regularFee =
    order?.regularShippingFee ?? shippingFees?.regularShippingFee ?? 0;
  const rushFee = order?.rushShippingFee ?? shippingFees?.rushShippingFee ?? 0;
  const totalDeliveryFee = regularFee + rushFee;

  // 3. Tính VAT (10% của subtotal, phí vận chuyển không chịu thuế)
  const vat = order?.vat ?? subtotal * 0.1;

  // 4. Tính tổng tiền cuối cùng
  const total = order?.total ?? subtotal + totalDeliveryFee + vat;

  return (
    <Card>
      <CardContent>
        {showTitle && (
          <Typography variant="h6" gutterBottom>
            Tóm Tắt Đơn Hàng
          </Typography>
        )}

        {/* Danh sách sản phẩm */}
        {items.length > 0 && (
          <List dense={compact}>
            {items.map((item) => (
              <ListItem key={item.productDTO?.productID} sx={{ px: 0 }}>
                <ListItemAvatar>
                  <Avatar
                    src={item.productDTO?.imageURL}
                    alt={item.productDTO?.title}
                    sx={{
                      width: compact ? 40 : 56,
                      height: compact ? 40 : 56,
                      borderRadius: "8px",
                    }}
                    variant="square"
                  />
                </ListItemAvatar>
                <ListItemText
                  primary={
                    <Typography variant={compact ? "body2" : "body1"} noWrap>
                      {item.productDTO?.title}
                    </Typography>
                  }
                  secondary={`Số lượng: ${item.quantity}`}
                />
                <Typography
                  variant={compact ? "body2" : "body1"}
                  sx={{ fontWeight: "bold" }}
                >
                  {formatPrice((item.productDTO?.price || 0) * item.quantity)}
                </Typography>
              </ListItem>
            ))}
          </List>
        )}

        <Divider sx={{ my: 2 }} />

        {/* Chi tiết các loại phí */}
        <Box sx={{ space: 1 }}>
          <Box sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}>
            <Typography variant="body2">Tạm tính:</Typography>
            <Typography variant="body2">{formatPrice(subtotal)}</Typography>
          </Box>

          {/* Hiển thị phí vận chuyển một cách linh hoạt */}
          {rushFee > 0 ? (
            <>
              <Box
                sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}
              >
                <Typography variant="body2">
                  Phí vận chuyển (thường):
                </Typography>
                <Typography variant="body2">
                  {formatPrice(regularFee)}
                </Typography>
              </Box>
              <Box
                sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}
              >
                <Typography variant="body2">
                  Phí vận chuyển (hỏa tốc):
                </Typography>
                <Typography variant="body2">{formatPrice(rushFee)}</Typography>
              </Box>
            </>
          ) : (
            <Box
              sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}
            >
              <Typography variant="body2">Phí vận chuyển:</Typography>
              <Typography variant="body2">{formatPrice(regularFee)}</Typography>
            </Box>
          )}

          <Box sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}>
            <Typography variant="body2">VAT (10%):</Typography>
            <Typography variant="body2">{formatPrice(vat)}</Typography>
          </Box>

          <Divider sx={{ my: 1 }} />

          <Box sx={{ display: "flex", justifyContent: "space-between" }}>
            <Typography variant="h6" sx={{ fontWeight: "bold" }}>
              Tổng cộng:
            </Typography>
            <Typography
              variant="h6"
              sx={{ fontWeight: "bold", color: "primary.main" }}
            >
              {formatPrice(total)}
            </Typography>
          </Box>
        </Box>

        {/* Thông tin giao hàng hỏa tốc */}
        {deliveryInfo?.isRushOrder && (
          <Box sx={{ mt: 2, p: 2, bgcolor: "info.light", borderRadius: 1 }}>
            <Typography
              variant="subtitle2"
              gutterBottom
              sx={{ color: "info.dark", fontWeight: "bold" }}
            >
              🚀 Giao hàng hỏa tốc
            </Typography>
            {deliveryInfo.deliveryTime && (
              <Typography variant="body2" sx={{ color: "info.dark" }}>
                Thời gian mong muốn: {deliveryInfo.deliveryTime}
              </Typography>
            )}
            {deliveryInfo.specialInstructions && (
              <Typography variant="body2" sx={{ color: "info.dark" }}>
                Ghi chú: {deliveryInfo.specialInstructions}
              </Typography>
            )}
          </Box>
        )}
      </CardContent>
    </Card>
  );
};

export default OrderSummary;
