import React, { useState, useEffect } from "react";
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
import { ArrowBack, ShoppingCart, CreditCard, CheckCircle } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { useCart } from "../../contexts/CartContext";
import DeliveryForm from "../../components/Order/DeliveryForm";
import OrderSummary from "../../components/Order/OrderSummary";
import { orderService } from "../../services/orderService";

const CheckoutPage = () => {
    const navigate = useNavigate();
    const { cartItems, getSelectedItems, getSelectedCartTotal, clearCart, validateCartStock } = useCart();

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
        contactPerson: "",
        contactPhone: "",
        buildingAccess: "",
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

    const steps = ["Delivery Information", "Payment Method", "Confirmation"];

    // Get selected items for checkout
    const selectedItems = getSelectedItems();

    useEffect(() => {
        if (cartItems.length === 0) {
            navigate("/cart");
            return;
        }

        if (selectedItems.length === 0) {
            navigate("/cart");
            return;
        }

        // Validate stock for selected items when entering checkout
        const validateStockOnLoad = async () => {
            try {
                const validation = await validateCartStock();
                if (!validation.isValid) {
                    setSnackbar({
                        open: true,
                        message: "Một số sản phẩm đã hết hàng. Vui lòng quay lại giỏ hàng để kiểm tra.",
                        severity: "error",
                    });
                    setTimeout(() => navigate("/cart"), 2000);
                }
            } catch (error) {
                console.error("Stock validation failed:", error);
            }
        };

        validateStockOnLoad();
    }, [cartItems, selectedItems, navigate, validateCartStock]);

    const validateDeliveryInfo = () => {
        const newErrors = {};

        if (!deliveryInfo.recipientName?.trim()) {
            newErrors.recipientName = "Recipient name is required";
        }

        if (!deliveryInfo.phoneNumber?.trim()) {
            newErrors.phoneNumber = "Phone number is required";
        } else if (!/^\d{10,11}$/.test(deliveryInfo.phoneNumber.replace(/\s/g, ""))) {
            newErrors.phoneNumber = "Please enter a valid phone number";
        }

        if (deliveryInfo.mail && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(deliveryInfo.mail)) {
            newErrors.mail = "Please enter a valid email address";
        }

        if (!deliveryInfo.city) {
            newErrors.city = "City/Province is required";
        }

        if (!deliveryInfo.district) {
            newErrors.district = "District is required";
        }

        if (!deliveryInfo.addressDetail?.trim()) {
            newErrors.addressDetail = "Detailed address is required";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleNext = () => {
        if (activeStep === 0) {
            if (!validateDeliveryInfo()) {
                setSnackbar({
                    open: true,
                    message: "Please fill in all required fields correctly",
                    severity: "error",
                });
                return;
            }
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
            // Gọi API tạo đơn hàng
            const orderData = {
                cartItems: selectedItems.map((item) => ({
                    productDTO: item.productDTO, // Use productDTO instead of product
                    quantity: item.quantity,
                })),
                deliveryInfo: {
                    city: deliveryInfo.city,
                    district: deliveryInfo.district,
                    addressDetail: deliveryInfo.addressDetail,
                    recipientName: deliveryInfo.recipientName,
                    mail: deliveryInfo.mail,
                    phoneNumber: deliveryInfo.phoneNumber,
                    isRushOrder: deliveryInfo.isRushOrder,
                    deliveryTime: deliveryInfo.deliveryTime,
                    specialInstructions: deliveryInfo.specialInstructions,
                    contactPerson: deliveryInfo.contactPerson,
                    contactPhone: deliveryInfo.contactPhone,
                    buildingAccess: deliveryInfo.buildingAccess,
                },
            };
            const createdOrder = await orderService.createOrder(orderData);

            // Clear cart
            clearCart();

            // Navigate to success page
            navigate("/order-success", {
                state: {
                    orderId: createdOrder.id,
                    orderData: createdOrder,
                },
            });
        } catch (error) {
            setSnackbar({
                open: true,
                message: error.message || "Failed to place order. Please try again.",
                severity: "error",
            });
        } finally {
            setLoading(false);
            setConfirmDialog(false);
        }
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(price);
    };

    const renderStepContent = (step) => {
        switch (step) {
            case 0:
                return <DeliveryForm deliveryInfo={deliveryInfo} onDeliveryInfoChange={setDeliveryInfo} errors={errors} />;
            case 1:
                return (
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Payment Method
                            </Typography>
                            <Alert severity="info" sx={{ mt: 2 }}>
                                <Typography variant="body2">
                                    Payment via VNPay/Momo are currently the only available payment method. You will pay securely through VNPay/Momo
                                    at the time of placing your order.
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
                                    Pay in advance via VNPay/Momo. You will be redirected to the VNPay/Momo payment gateway to complete the
                                    transaction.
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
                                Please review your order details before placing the order.
                            </Typography>

                            {/* Delivery Summary */}
                            <Box sx={{ mb: 3, p: 2, bgcolor: "grey.50", borderRadius: 1 }}>
                                <Typography variant="subtitle1" gutterBottom sx={{ fontWeight: "medium" }}>
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
                                    <strong>Address:</strong> {deliveryInfo.addressDetail}, {deliveryInfo.district}, {deliveryInfo.city}
                                </Typography>
                                <Typography variant="body2">
                                    <strong>Delivery:</strong>{" "}
                                    {deliveryInfo.isRushOrder ? "Rush Delivery (Same day)" : "Standard Delivery (3-5 days)"}
                                </Typography>
                            </Box>

                            <Alert severity="success" sx={{ mt: 2 }}>
                                <Typography variant="body2">
                                    Your order will be processed immediately after confirmation. You will receive a confirmation message shortly.
                                </Typography>
                            </Alert>
                        </CardContent>
                    </Card>
                );
            default:
                return null;
        }
    };

    if (cartItems.length === 0) {
        return null; // Will redirect in useEffect
    }

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            {/* Header */}
            <Box sx={{ mb: 4 }}>
                <Button startIcon={<ArrowBack />} onClick={() => navigate("/cart")} sx={{ mb: 2 }}>
                    Back to Cart
                </Button>
                <Typography variant="h4" component="h1" gutterBottom>
                    Checkout
                </Typography>
                <Typography variant="body1" color="text.secondary">
                    Complete your order in a few simple steps.
                </Typography>
            </Box>

            {/* Stepper */}
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

            {/* Main Content */}
            <Grid container spacing={4}>
                {/* Left Column - Forms */}
                <Grid item xs={12} md={8}>
                    {renderStepContent(activeStep)}
                </Grid>

                {/* Right Column - Order Summary */}
                <Grid item xs={12} md={4}>
                    <OrderSummary
                        items={selectedItems}
                        order={{
                            deliveryFee: 50000, // Fixed delivery fee, no rush fee
                        }}
                        deliveryInfo={deliveryInfo}
                    />
                </Grid>
            </Grid>

            {/* Navigation Buttons */}
            <Box sx={{ display: "flex", justifyContent: "space-between", mt: 4 }}>
                <Button onClick={handleBack} sx={{ mr: 1 }}>
                    {activeStep === 0 ? "Back to Cart" : "Back"}
                </Button>
                <Button variant="contained" onClick={handleNext} startIcon={activeStep === steps.length - 1 ? <CheckCircle /> : <ShoppingCart />}>
                    {activeStep === steps.length - 1 ? "Place Order" : "Continue"}
                </Button>
            </Box>

            {/* Confirmation Dialog */}
            <Dialog open={confirmDialog} onClose={() => setConfirmDialog(false)} maxWidth="sm" fullWidth>
                <DialogTitle>Confirm Your Order</DialogTitle>
                <DialogContent>
                    <Typography gutterBottom>Are you sure you want to place this order?</Typography>
                    <Box sx={{ mt: 2, p: 2, bgcolor: "grey.50", borderRadius: 1 }}>
                        <Typography variant="body2">
                            <strong>Total Amount:</strong> {formatPrice(getSelectedCartTotal())}
                        </Typography>
                        <Typography variant="body2">
                            <strong>Items:</strong> {selectedItems.length} product
                            {selectedItems.length !== 1 ? "s" : ""}
                        </Typography>
                        <Typography variant="body2">
                            <strong>Delivery:</strong> {deliveryInfo.isRushOrder ? "Rush Delivery (Same day)" : "Standard Delivery (3-5 days)"}
                        </Typography>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setConfirmDialog(false)}>Cancel</Button>
                    <Button
                        onClick={handlePlaceOrder}
                        variant="contained"
                        disabled={loading}
                        startIcon={loading ? <CircularProgress size={20} /> : <CheckCircle />}
                    >
                        {loading ? "Processing..." : "Confirm Order"}
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Snackbar */}
            <Snackbar
                open={snackbar.open}
                autoHideDuration={4000}
                onClose={() => setSnackbar({ ...snackbar, open: false })}
                anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
            >
                <Alert onClose={() => setSnackbar({ ...snackbar, open: false })} severity={snackbar.severity}>
                    {snackbar.message}
                </Alert>
            </Snackbar>

            {/* Inventory Error Dialog */}
            <Dialog
                open={inventoryErrorDialog.open}
                onClose={() => setInventoryErrorDialog({ ...inventoryErrorDialog, open: false })}
                maxWidth="sm"
                fullWidth
            >
                <DialogTitle>Inventory Error</DialogTitle>
                <DialogContent>
                    <Typography gutterBottom>
                        Một số sản phẩm trong giỏ hàng không đủ tồn kho. Vui lòng cập nhật số lượng hoặc xóa sản phẩm:
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
                                        <strong>Tồn kho hiện tại:</strong> {item.productDTO.quantity}
                                    </Typography>
                                    <Typography variant="body2" color="error.main">
                                        <strong>Số lượng thiếu:</strong> {item.quantity - item.productDTO.quantity}
                                    </Typography>
                                </Box>
                            </Box>
                        ))}
                    </Box>
                    <Alert severity="warning" sx={{ mt: 2 }}>
                        <Typography variant="body2">
                            Vui lòng quay lại giỏ hàng để cập nhật số lượng sản phẩm hoặc xóa các sản phẩm không đủ tồn kho.
                        </Typography>
                    </Alert>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setInventoryErrorDialog({ open: false, insufficientItems: [] })} variant="contained">
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
