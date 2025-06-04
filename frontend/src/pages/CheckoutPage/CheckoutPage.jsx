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
import { useAuth } from "../../contexts/AuthContext";
import DeliveryForm from "../../components/Order/DeliveryForm";
import OrderSummary from "../../components/Order/OrderSummary";

const CheckoutPage = () => {
    const navigate = useNavigate();
    const { cartItems, getCartTotal, clearCart } = useCart();
    const { isAuthenticated } = useAuth();

    const [activeStep, setActiveStep] = useState(0);
    const [deliveryInfo, setDeliveryInfo] = useState({
        recipientName: "",
        phoneNumber: "",
        mail: "",
        city: "",
        district: "",
        addressDetail: "",
        isRushOrder: false,
    });
    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });
    const [confirmDialog, setConfirmDialog] = useState(false);

    const steps = ["Delivery Information", "Payment Method", "Confirmation"];

    useEffect(() => {
        if (!isAuthenticated()) {
            navigate("/login");
            return;
        }

        if (cartItems.length === 0) {
            navigate("/cart");
            return;
        }
    }, [isAuthenticated, cartItems, navigate]);

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
            // Simulate API call
            await new Promise((resolve) => setTimeout(resolve, 2000));

            const orderData = {
                id: `ORDER-${Date.now()}`,
                items: cartItems,
                deliveryInfo,
                total: getCartTotal(),
                date: new Date().toISOString(),
                status: "PENDING",
            };

            // Store order in localStorage for demo
            const existingOrders = JSON.parse(localStorage.getItem("userOrders") || "[]");
            existingOrders.push(orderData);
            localStorage.setItem("userOrders", JSON.stringify(existingOrders));

            // Clear cart
            clearCart();

            // Navigate to success page
            navigate("/order-success", {
                state: {
                    orderId: orderData.id,
                    orderData,
                },
            });
        } catch (error) {
            setSnackbar({
                open: true,
                message: "Failed to place order. Please try again.",
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
                                    Payment via VNPay is currently the only available payment method. You will pay securely through VNPay at the time of placing your order.
                                </Typography>
                            </Alert>
                            <Box sx={{ mt: 3, p: 2, border: 1, borderColor: "primary.main", borderRadius: 1 }}>
                                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                                    <CreditCard color="primary" sx={{ mr: 1 }} />
                                    <Typography variant="h6">Payment via VNPay</Typography>
                                </Box>
                                <Typography variant="body2" color="text.secondary">
                                    Pay in advance via VNPay. You will be redirected to the VNPay payment gateway to complete the transaction.
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
                                    <strong>Delivery Type:</strong>{" "}
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
                        items={cartItems}
                        order={{
                            deliveryFee: deliveryInfo.isRushOrder ? 100000 : 50000,
                        }}
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
                            <strong>Total Amount:</strong> {formatPrice(getCartTotal())}
                        </Typography>
                        <Typography variant="body2">
                            <strong>Items:</strong> {cartItems.length} product{cartItems.length !== 1 ? "s" : ""}
                        </Typography>
                        <Typography variant="body2">
                            <strong>Delivery:</strong> {deliveryInfo.isRushOrder ? "Rush (Same day)" : "Standard (3-5 days)"}
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
        </Container>
    );
};

export default CheckoutPage;
