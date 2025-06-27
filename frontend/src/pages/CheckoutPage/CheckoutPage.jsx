import React, { useState, useEffect, useMemo } from "react";
import {
  Container,
  Typography,
  Grid,
  Button,
  Box,
  Card,
  CardContent,
  Stepper,
  Step,
  StepLabel,
  Alert,
  Snackbar,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  CircularProgress,
} from "@mui/material";
import {
  ArrowBack,
  ShoppingCart,
  CreditCard,
  CheckCircle,
} from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { useCart } from "../../contexts/CartContext";
import DeliveryForm from "../../components/Order/DeliveryForm";
import OrderSummary from "../../components/Order/OrderSummary";
import { orderService } from "../../services/orderService";

const CheckoutPage = () => {
  const navigate = useNavigate();
  const { cartItems, getSelectedItems, getSelectedCartTotal, clearCart } =
    useCart();

  const [activeStep, setActiveStep] = useState(0);
  const [deliveryInfo, setDeliveryInfo] = useState({
    recipientName: "",
    phoneNumber: "",
    mail: "",
    city: "",
    district: "",
    addressDetail: "",
    isRushOrder: false,
    deliveryTime: "",
    specialInstructions: "",
  });
  const [shippingFees, setShippingFees] = useState({
    regularShippingFee: 0,
    rushShippingFee: 0,
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "success",
  });
  const [confirmDialog, setConfirmDialog] = useState(false);
  const [inventoryErrorDialog, setInventoryErrorDialog] = useState({
    open: false,
    insufficientItems: [],
  });

  const steps = ["Thông tin giao hàng", "Thanh toán", "Xác nhận"];

  // Memoize selectedItems để tránh tính toán lại không cần thiết
  const selectedItems = useMemo(() => getSelectedItems(), [cartItems]);

  // Fetch phí vận chuyển mỗi khi thông tin giao hàng hoặc sản phẩm thay đổi
  useEffect(() => {
    if (selectedItems.length === 0) {
      navigate("/cart");
      return;
    }

    const fetchShippingFees = async () => {
      try {
        const orderData = {
          cartItems: selectedItems.map((item) => ({
            productDTO: item.productDTO,
            quantity: item.quantity,
          })),
          deliveryInfo: deliveryInfo,
        };
        // Giả định orderService.calculateShippingFees gọi API và trả về { regularShippingFee, rushShippingFee }
        const fees = await orderService.calculateShippingFees(orderData);
        setShippingFees(fees);
      } catch (error) {
        console.error("Failed to fetch shipping fees:", error);
        setShippingFees({ regularShippingFee: 0, rushShippingFee: 0 }); // Fallback
      }
    };

    fetchShippingFees();
  }, [deliveryInfo, selectedItems, navigate]);

  const validateDeliveryInfo = () => {
    const newErrors = {};

    if (!deliveryInfo.recipientName?.trim()) {
      newErrors.recipientName = "Vui lòng nhập tên người nhận";
    }

    if (!deliveryInfo.phoneNumber?.trim()) {
      newErrors.phoneNumber = "Vui lòng nhập số điện thoại";
    } else if (
      !/^\d{10,11}$/.test(deliveryInfo.phoneNumber.replace(/\s/g, ""))
    ) {
      newErrors.phoneNumber = "Số điện thoại không hợp lệ";
    }

    if (
      deliveryInfo.mail &&
      !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(deliveryInfo.mail)
    ) {
      newErrors.mail = "Địa chỉ email không hợp lệ";
    }

    if (!deliveryInfo.city) {
      newErrors.city = "Vui lòng chọn Tỉnh/Thành phố";
    }

    if (!deliveryInfo.district) {
      newErrors.district = "Vui lòng chọn Quận/Huyện";
    }

    if (!deliveryInfo.addressDetail?.trim()) {
      newErrors.addressDetail = "Vui lòng nhập địa chỉ chi tiết";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleNext = () => {
    if (activeStep === 0 && !validateDeliveryInfo()) {
      setSnackbar({
        open: true,
        message: "Vui lòng điền đầy đủ các thông tin bắt buộc",
        severity: "error",
      });
      return;
    }
    if (activeStep === steps.length - 1) {
      setConfirmDialog(true);
    } else {
      setActiveStep((prevStep) => prevStep + 1);
    }
  };

  const handleBack = () => {
    if (activeStep === 0) {
      navigate("/cart");
    } else {
      setActiveStep((prevStep) => prevStep - 1);
    }
  };

  const handlePlaceOrder = async () => {
    setLoading(true);
    try {
      console.log("Bắt đầu gọi API tạo đơn hàng...");
      const orderData = {
        cartItems: selectedItems.map((item) => ({
          productDTO: item.productDTO,
          quantity: item.quantity,
        })),
        deliveryInfo: deliveryInfo,
      };

      const createdOrder = await orderService.createOrder(orderData);
      // Kiểm tra xem dữ liệu trả về có hợp lệ không
      console.log("API trả về thành công:", createdOrder);

      if (!createdOrder || !createdOrder.id) {
        // Ném ra một lỗi nếu dữ liệu không hợp lệ
        throw new Error("Dữ liệu đơn hàng trả về không hợp lệ.");
      }

      console.log("Chuẩn bị xóa giỏ hàng...");
      clearCart();
      console.log("Đã xóa giỏ hàng. Chuẩn bị chuyển hướng...");

      navigate("/order-success", {
        state: { orderId: createdOrder.id, orderData: createdOrder },
      });
      console.log("Lệnh chuyển hướng đã được gọi.");
    } catch (error) {
      // Log toàn bộ lỗi ra để xem chi tiết
      console.error("Lỗi khi đặt hàng:", error);

      setSnackbar({
        open: true,
        message: error.message || "Đặt hàng thất bại. Vui lòng thử lại.",
        severity: "error",
      });
    } finally {
      setLoading(false);
      setConfirmDialog(false);
    }
  };
  // Tính toán tổng tiền cuối cùng cho dialog xác nhận
  const subtotal = getSelectedCartTotal();
  const totalDeliveryFee =
    shippingFees.regularShippingFee + shippingFees.rushShippingFee;
  const vat = subtotal * 0.1;
  const finalTotal = subtotal + totalDeliveryFee + vat;

  const renderStepContent = (step) => {
    switch (step) {
      case 0:
        return (
          <DeliveryForm
            deliveryInfo={deliveryInfo}
            onDeliveryInfoChange={setDeliveryInfo}
            errors={errors}
          />
        );
      case 1:
        return (
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Phương thức thanh toán
              </Typography>
              <Alert severity="info" sx={{ mt: 2 }}>
                <Typography variant="body2">
                  Thanh toán qua VNPay/Momo là phương thức duy nhất hiện có. Bạn
                  sẽ thanh toán an toàn qua cổng thanh toán khi đặt hàng.
                </Typography>
              </Alert>
              <Box
                sx={{
                  mt: 3,
                  p: 2,
                  border: 1,
                  borderColor: "primary.main",
                  borderRadius: 1,
                }}
              >
                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                  <CreditCard color="primary" sx={{ mr: 1 }} />
                  <Typography variant="h6">
                    Thanh toán qua VNPay/Momo
                  </Typography>
                </Box>
                <Typography variant="body2" color="text.secondary">
                  Thanh toán trước qua VNPay/Momo. Bạn sẽ được chuyển hướng đến
                  cổng thanh toán để hoàn tất giao dịch.
                </Typography>
              </Box>
            </CardContent>
          </Card>
        );
      case 2:
        return (
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Xác nhận đơn hàng
              </Typography>
              <Typography variant="body2" color="text.secondary" paragraph>
                Vui lòng kiểm tra lại thông tin trước khi hoàn tất đặt hàng.
              </Typography>
              <Box sx={{ mb: 3, p: 2, bgcolor: "grey.50", borderRadius: 1 }}>
                <Typography
                  variant="subtitle1"
                  gutterBottom
                  sx={{ fontWeight: "medium" }}
                >
                  Thông tin giao hàng
                </Typography>
                <Typography variant="body2">
                  <strong>Người nhận:</strong> {deliveryInfo.recipientName}
                </Typography>
                <Typography variant="body2">
                  <strong>Điện thoại:</strong> {deliveryInfo.phoneNumber}
                </Typography>
                {deliveryInfo.mail && (
                  <Typography variant="body2">
                    <strong>Email:</strong> {deliveryInfo.mail}
                  </Typography>
                )}
                <Typography variant="body2">
                  <strong>Địa chỉ:</strong>{" "}
                  {`${deliveryInfo.addressDetail}, ${deliveryInfo.district}, ${deliveryInfo.city}`}
                </Typography>
                <Typography variant="body2">
                  <strong>Giao hàng:</strong>{" "}
                  {deliveryInfo.isRushOrder
                    ? "Giao hàng hỏa tốc"
                    : "Giao hàng tiêu chuẩn"}
                </Typography>
              </Box>
              <Alert severity="success" sx={{ mt: 2 }}>
                <Typography variant="body2">
                  Đơn hàng của bạn sẽ được xử lý ngay sau khi xác nhận.
                </Typography>
              </Alert>
            </CardContent>
          </Card>
        );
      default:
        return null;
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate("/cart")}
          sx={{ mb: 2 }}
        >
          Quay lại giỏ hàng
        </Button>
        <Typography variant="h4" component="h1" gutterBottom>
          Thanh Toán
        </Typography>
      </Box>

      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Stepper activeStep={activeStep} alternativeLabel>
            {steps.map((label) => (
              <Step key={label}>
                <StepLabel>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>
        </CardContent>
      </Card>

      <Grid container spacing={4}>
        <Grid item xs={12} md={8}>
          {renderStepContent(activeStep)}
        </Grid>
        <Grid item xs={12} md={4}>
          <OrderSummary
            items={selectedItems}
            shippingFees={shippingFees}
            deliveryInfo={deliveryInfo}
          />
        </Grid>
      </Grid>

      <Box sx={{ display: "flex", justifyContent: "space-between", mt: 4 }}>
        <Button onClick={handleBack}>
          {activeStep === 0 ? "Quay lại giỏ hàng" : "Quay lại"}
        </Button>
        <Button
          variant="contained"
          onClick={handleNext}
          startIcon={
            activeStep === steps.length - 1 ? <CheckCircle /> : <ShoppingCart />
          }
        >
          {activeStep === steps.length - 1 ? "Đặt Hàng" : "Tiếp Tục"}
        </Button>
      </Box>

      <Dialog
        open={confirmDialog}
        onClose={() => setConfirmDialog(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Xác nhận đơn hàng</DialogTitle>
        <DialogContent>
          <Typography gutterBottom>
            Bạn có chắc chắn muốn đặt đơn hàng này?
          </Typography>
          <Box sx={{ mt: 2, p: 2, bgcolor: "grey.50", borderRadius: 1 }}>
            <Typography variant="body2">
              <strong>Tổng tiền hàng:</strong> {subtotal} VND
            </Typography>
            <Typography variant="body2">
              <strong>Phí vận chuyển:</strong> {totalDeliveryFee} VND
            </Typography>
            <Typography variant="body2">
              <strong>VAT:</strong> {vat} VND
            </Typography>
            <Typography variant="h6" sx={{ mt: 1 }}>
              <strong>Thanh toán:</strong> {finalTotal} VND
            </Typography>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmDialog(false)}>Hủy</Button>
          <Button
            onClick={handlePlaceOrder}
            variant="contained"
            disabled={loading}
            startIcon={
              loading ? <CircularProgress size={20} /> : <CheckCircle />
            }
          >
            {loading ? "Đang xử lý..." : "Xác nhận"}
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
      >
        <Alert
          onClose={() => setSnackbar({ ...snackbar, open: false })}
          severity={snackbar.severity}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>

      <Dialog
        open={inventoryErrorDialog.open}
        onClose={() =>
          setInventoryErrorDialog({ ...inventoryErrorDialog, open: false })
        }
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Lỗi Tồn Kho</DialogTitle>
        <DialogContent>
          <Typography gutterBottom>
            Một số sản phẩm trong giỏ hàng không đủ tồn kho. Vui lòng cập nhật
            số lượng hoặc xóa sản phẩm:
          </Typography>
          <Box sx={{ mt: 2 }}>
            {inventoryErrorDialog.insufficientItems.map((item, index) => (
              <Box
                key={index}
                sx={{
                  p: 2,
                  mb: 1,
                  border: 1,
                  borderColor: "error.light",
                  borderRadius: 1,
                  bgcolor: "error.50",
                }}
              >
                <Typography variant="subtitle2" sx={{ fontWeight: "bold" }}>
                  {item.productDTO.title}
                </Typography>
                <Box sx={{ mt: 1 }}>
                  <Typography variant="body2" color="text.secondary">
                    <strong>Số lượng yêu cầu:</strong> {item.quantity}
                  </Typography>
                  <Typography variant="body2" color="error.main">
                    <strong>Tồn kho hiện tại:</strong>{" "}
                    {item.productDTO.quantity}
                  </Typography>
                  <Typography variant="body2" color="error.main">
                    <strong>Số lượng thiếu:</strong>{" "}
                    {item.quantity - item.productDTO.quantity}
                  </Typography>
                </Box>
              </Box>
            ))}
          </Box>
          <Alert severity="warning" sx={{ mt: 2 }}>
            <Typography variant="body2">
              Vui lòng quay lại giỏ hàng để cập nhật số lượng sản phẩm.
            </Typography>
          </Alert>
        </DialogContent>
        <DialogActions>
          <Button
            onClick={() =>
              setInventoryErrorDialog({ open: false, insufficientItems: [] })
            }
            variant="contained"
          >
            Đóng
          </Button>
          <Button
            onClick={() => {
              setInventoryErrorDialog({ open: false, insufficientItems: [] });
              navigate("/cart");
            }}
            variant="outlined"
            color="primary"
          >
            Quay lại giỏ hàng
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default CheckoutPage;
