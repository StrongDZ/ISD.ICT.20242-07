import React from "react";
import {
    Container,
    Box,
    Typography,
    Card,
    CardContent,
    Divider,
    Button,
    Grid,
    Avatar,
} from "@mui/material";
import ReceiptIcon from "@mui/icons-material/Receipt";
import CreditCardIcon from "@mui/icons-material/CreditCard";

const InfoRow = ({ label, value }) => (
    <Box sx={{ display: "flex", justifyContent: "space-between", my: 1 }}>
        <Typography variant="body2" color="text.secondary">
            {label}
        </Typography>
        <Typography variant="body2" fontWeight="500">
            {value}
        </Typography>
    </Box>
);

const PaymentSuccess = ({ order, payment, onContinueShopping, onViewOrders }) => {
    return (
        <Box sx={{ textAlign: "center", mt: 6 , mb: 4 }}>
            <Typography variant="h4" gutterBottom color="primary">
                🎉 Payment Successful!
            </Typography>
            <Typography variant="subtitle1" color="text.secondary" sx={{ mb: 3 }}>
                Thank you for your purchase. Here are the order and payment details.
            </Typography>
            <Container maxWidth="md">
                <Grid container spacing={4}>
                    {/* Order Information */}
                    <Grid item xs={12} md={6}>
                        <Card sx={{ borderRadius: 3, boxShadow: 3 }}>
                            <CardContent>
                                <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
                                    <Avatar sx={{ bgcolor: "primary.main", mr: 2 }}>
                                        <ReceiptIcon />
                                    </Avatar>
                                    <Typography variant="h6">
                                        Order Information
                                    </Typography>
                                </Box>
                                <Divider sx={{ mb: 2 }} />
                                <InfoRow label="Order ID" value={order?.id || "N/A"} />
                                <InfoRow label="Customer" value={order?.customerName || "N/A"} />
                                <InfoRow label="Email" value={order?.customerEmail || "N/A"} />
                                <InfoRow label="Total Amount" value={`${order?.total?.toLocaleString()}₫`} />
                                <InfoRow label="Status" value={order?.status || "N/A"} />
                                <InfoRow label="Created At" value={order?.createdAt || "N/A"} />
                            </CardContent>
                        </Card>
                    </Grid>
                    {/* Payment Information */}
                    <Grid item xs={12} md={6}>
                        <Card sx={{ borderRadius: 3, boxShadow: 3 }}>
                            <CardContent>
                                <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
                                    <Avatar sx={{ bgcolor: "success.main", mr: 2 }}>
                                        <CreditCardIcon />
                                    </Avatar>
                                    <Typography variant="h6">
                                        Payment Details
                                    </Typography>
                                </Box>
                                <Divider sx={{ mb: 2 }} />
                                <InfoRow label="Transaction ID" value={payment?.transactionId || "N/A"} />
                                <InfoRow label="Method" value={payment?.method || "VNPay"} />
                                <InfoRow label="Amount Paid" value={`${payment?.amount?.toLocaleString()}₫`} />
                                <InfoRow label="Paid At" value={payment?.paidAt || "N/A"} />
                                <InfoRow label="Status" value={payment?.status || "Success"} />
                            </CardContent>
                        </Card>
                    </Grid>
                </Grid>
            </Container>

            {/* Action Buttons */}
            <Box sx={{ mt: 5, display: "flex", justifyContent: "center", gap: 3 }}>
                <Button
                    variant="contained"
                    color="primary"
                    size="large"
                    onClick={onContinueShopping}
                    sx={{ px: 4 }}
                >
                    Continue Shopping
                </Button>
                <Button
                    variant="outlined"
                    color="secondary"
                    size="large"
                    onClick={onViewOrders}
                    sx={{ px: 4 }}
                >
                    View Orders
                </Button>
            </Box>
        </Box >
    );
};

export default PaymentSuccess;
