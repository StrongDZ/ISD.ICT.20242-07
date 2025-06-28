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

  const steps = ["Delivery Information", "Payment", "Confirmation"];

  // Memoize selectedItems to avoid unnecessary recalculations
  const selectedItems = useMemo(() => getSelectedItems(), [cartItems]);

  // Fetch shipping fees whenever delivery info or products change
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
        // Assume orderService.calculateShippingFees calls API and returns { regularShippingFee, rushShippingFee }
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
      newErrors.recipientName = "Please enter recipient name";
    }

    if (!deliveryInfo.phoneNumber?.trim()) {
      newErrors.phoneNumber = "Please enter phone number";
    } else if (
      !/^\d{10,11}$/.test(deliveryInfo.phoneNumber.replace(/\s/g, ""))
    ) {
      newErrors.phoneNumber = "Invalid phone number";
    }

    if (
      deliveryInfo.mail &&
      !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(deliveryInfo.mail)
    ) {
      newErrors.mail = "Invalid email address";
    }

    if (!deliveryInfo.city) {
      newErrors.city = "Please select Province/City";
    }

    if (!deliveryInfo.district) {
      newErrors.district = "Please select District";
    }

    if (!deliveryInfo.addressDetail?.trim()) {
      newErrors.addressDetail = "Please enter detailed address";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleNext = () => {
    if (activeStep === 0 && !validateDeliveryInfo()) {
      setSnackbar({
        open: true,
        message: "Please fill in all required information",
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
      console.log("Starting API call to create order...");
      const orderData = {
        cartItems: selectedItems.map((item) => ({
          productDTO: item.productDTO,
          quantity: item.quantity,
        })),
        deliveryInfo: deliveryInfo,
      };

      const createdOrder = await orderService.createOrder(orderData);
      // Check if the returned data is valid
      console.log("API returned successfully:", createdOrder);

      if (!createdOrder || !createdOrder.id) {
        // Throw an error if the returned data is invalid
        throw new Error("Invalid order data returned.");
      }

      console.log("Preparing to clear cart...");
      clearCart();
      console.log("Cart cleared. Preparing to redirect...");

      navigate("/order-success", {
        state: { orderId: createdOrder.id, orderData: createdOrder },
      });
      console.log("Redirect command called.");
    } catch (error) {
      // Log the entire error to see details
      console.error("Error when placing order:", error);

      setSnackbar({
        open: true,
        message: error.message || "Order placement failed. Please try again.",
        severity: "error",
      });
    } finally {
      setLoading(false);
      setConfirmDialog(false);
    }
  };
  // Calculate final total for confirmation dialog
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
                Payment Method
              </Typography>
              <Alert severity="info" sx={{ mt: 2 }}>
                <Typography variant="body2">
                  Payment via VNPay/Momo is the only available method. You will
                  pay securely through the payment gateway when placing your
                  order.
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
                  <Typography variant="h6">Payment via VNPay/Momo</Typography>
                </Box>
                <Typography variant="body2" color="text.secondary">
                  Pre-payment via VNPay/Momo. You will be redirected to the
                  payment gateway to complete the transaction.
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
                Order Confirmation
              </Typography>
              <Typography variant="body2" color="text.secondary" paragraph>
                Please review the information before completing your order.
              </Typography>
              <Box sx={{ mb: 3, p: 2, bgcolor: "grey.50", borderRadius: 1 }}>
                <Typography
                  variant="subtitle1"
                  gutterBottom
                  sx={{ fontWeight: "medium" }}
                >
                  Delivery Information
                </Typography>
                <Typography variant="body2">
                  <strong>Recipient:</strong> {deliveryInfo.recipientName}
                </Typography>
                <Typography variant="body2">
                  <strong>Phone:</strong> {deliveryInfo.phoneNumber}
                </Typography>
                {deliveryInfo.mail && (
                  <Typography variant="body2">
                    <strong>Email:</strong> {deliveryInfo.mail}
                  </Typography>
                )}
                <Typography variant="body2">
                  <strong>Address:</strong>{" "}
                  {`${deliveryInfo.addressDetail}, ${deliveryInfo.district}, ${deliveryInfo.city}`}
                </Typography>
                <Typography variant="body2">
                  <strong>Delivery:</strong>{" "}
                  {deliveryInfo.isRushOrder
                    ? "Express delivery"
                    : "Standard delivery"}
                </Typography>
              </Box>
              <Alert severity="success" sx={{ mt: 2 }}>
                <Typography variant="body2">
                  Your order will be processed immediately after confirmation.
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
          Back to Cart
        </Button>
        <Typography variant="h4" component="h1" gutterBottom>
          Checkout
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
          {activeStep === 0 ? "Back to Cart" : "Back"}
        </Button>
        <Button
          variant="contained"
          onClick={handleNext}
          startIcon={
            activeStep === steps.length - 1 ? <CheckCircle /> : <ShoppingCart />
          }
        >
          {activeStep === steps.length - 1 ? "Place Order" : "Continue"}
        </Button>
      </Box>

      <Dialog
        open={confirmDialog}
        onClose={() => setConfirmDialog(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Order Confirmation</DialogTitle>
        <DialogContent>
          <Typography gutterBottom>
            Are you sure you want to place this order?
          </Typography>
          <Box sx={{ mt: 2, p: 2, bgcolor: "grey.50", borderRadius: 1 }}>
            <Typography variant="body2">
              <strong>Subtotal:</strong> {subtotal} VND
            </Typography>
            <Typography variant="body2">
              <strong>Shipping Fee:</strong> {totalDeliveryFee} VND
            </Typography>
            <Typography variant="body2">
              <strong>VAT:</strong> {vat} VND
            </Typography>
            <Typography variant="h6" sx={{ mt: 1 }}>
              <strong>Total:</strong> {finalTotal} VND
            </Typography>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmDialog(false)}>Cancel</Button>
          <Button
            onClick={handlePlaceOrder}
            variant="contained"
            disabled={loading}
            startIcon={
              loading ? <CircularProgress size={20} /> : <CheckCircle />
            }
          >
            {loading ? "Processing..." : "Confirm"}
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
        <DialogTitle>Inventory Error</DialogTitle>
        <DialogContent>
          <Typography gutterBottom>
            Some products in your cart have insufficient inventory. Please
            update quantities or remove products:
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
                    <strong>Requested quantity:</strong> {item.quantity}
                  </Typography>
                  <Typography variant="body2" color="error.main">
                    <strong>Current inventory:</strong>{" "}
                    {item.productDTO.quantity}
                  </Typography>
                  <Typography variant="body2" color="error.main">
                    <strong>Shortage:</strong>{" "}
                    {item.quantity - item.productDTO.quantity}
                  </Typography>
                </Box>
              </Box>
            ))}
          </Box>
          <Alert severity="warning" sx={{ mt: 2 }}>
            <Typography variant="body2">
              Please return to cart to update product quantities.
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
            Close
          </Button>
          <Button
            onClick={() => {
              setInventoryErrorDialog({ open: false, insufficientItems: [] });
              navigate("/cart");
            }}
            variant="outlined"
            color="primary"
          >
            Back to Cart
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default CheckoutPage;
