import React, { useEffect, useState } from "react";
import axios from "axios";
import {
    Box, Typography, Grid, Card, CardContent, Avatar, Divider, CircularProgress,
    Container, Alert, Button, List, ListItem, ListItemText
} from "@mui/material";
import ReceiptIcon from "@mui/icons-material/Receipt";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import CreditCardIcon from "@mui/icons-material/CreditCard";
import { useLocation } from "react-router-dom";

// Component phụ để hiển thị thông tin hàng dọc
const InfoRow = ({ label, value }) => (
    <Box sx={{ display: "flex", justifyContent: "space-between", my: 0.5 }}>
        <Typography variant="body2" color="text.secondary">{label}</Typography>
        <Typography variant="body2" fontWeight={500}>{value}</Typography>
    </Box>
);

const PaymentHistory = () => {
    const location = useLocation();
    const [order, setOrder] = useState(null);
    const [orderItems, setOrderItems] = useState([]);
    const [transaction, setTransaction] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const API_BASE = "http://localhost:8080/api/payments";

    const orderId = new URLSearchParams(location.search).get("orderId");

    useEffect(() => {
        const fetchAll = async () => {
            try {
                const [orderRes, orderItemsRes, transRes] = await Promise.all([
                    axios.get(`${API_BASE}/order_info?id=${orderId}`),
                    axios.get(`${API_BASE}/order_product?id=${orderId}`),
                    axios.get(`${API_BASE}/transaction_history?orderId=${orderId}`),
                ]);

                if (orderRes.data.responseCode === 200) {
                    setOrder(orderRes.data.data);
                }

                if (orderItemsRes.data.responseCode === 200) {
                    setOrderItems(orderItemsRes.data.data);
                }

                if (transRes.data.responseCode === 200) {
                    setTransaction(transRes.data.data);
                }

            } catch (err) {
                console.error(err);
                setError("Failed to load payment history.");
            } finally {
                setLoading(false);
            }
        };

        if (orderId) {
            fetchAll();
        } else {
            setError("Missing orderId in URL.");
            setLoading(false);
        }
    }, [orderId]);

    const handleCancelOrder = async () => {
        try {
            const res = await axios.get(`${API_BASE}/cancel_order?id=${orderId}`);
            alert("Order canceled: " + res.data.data);
            window.location.reload();
        } catch (err) {
            alert("Failed to cancel order.");
        }
    };

    if (loading) {
        return (
            <Box sx={{ display: "flex", justifyContent: "center", mt: 10 }}>
                <CircularProgress />
                <Typography sx={{ ml: 2 }}>Loading...</Typography>
            </Box>
        );
    }

    if (error) {
        return (
            <Container maxWidth="sm" sx={{ mt: 6 }}>
                <Alert severity="error">{error}</Alert>
            </Container>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ mt: 4 }}>
            <Typography variant="h4" align="center" gutterBottom>
                🧾 Order & Payment Details
            </Typography>

            <Grid container spacing={4}>
                {/* Order Info */}
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Box display="flex" alignItems="center" mb={2}>
                                <Avatar sx={{ bgcolor: "primary.main", mr: 2 }}>
                                    <ReceiptIcon />
                                </Avatar>
                                <Typography variant="h6">Order Info</Typography>
                            </Box>
                            <Divider sx={{ mb: 2 }} />
                            <InfoRow label="Order ID" value={order?.orderID} />
                            <InfoRow label="Status" value={order?.status} />
                            <InfoRow label="Total" value={`${order?.totalAmount?.toLocaleString()}₫`} />

                            {order?.deliveryInfo && (
                                <>
                                    <InfoRow label="Recipient" value={order.deliveryInfo.recipientName} />
                                    <InfoRow label="Email" value={order.deliveryInfo.mail} />
                                    <InfoRow label="Phone" value={order.deliveryInfo.phoneNumber} />
                                    <InfoRow label="City" value={order.deliveryInfo.city} />
                                    <InfoRow label="District" value={order.deliveryInfo.district} />
                                    <InfoRow label="Address" value={order.deliveryInfo.addressDetail} />
                                </>
                            )}

                            {order?.status !== "APPROVED" && (
                                <Button
                                    variant="contained"
                                    color="error"
                                    fullWidth
                                    sx={{ mt: 2 }}
                                    onClick={handleCancelOrder}
                                >
                                    Cancel Order
                                </Button>
                            )}
                        </CardContent>
                    </Card>
                </Grid>

                {/* Product Info */}
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Box display="flex" alignItems="center" mb={2}>
                                <Avatar sx={{ bgcolor: "info.main", mr: 2 }}>
                                    <ShoppingCartIcon />
                                </Avatar>
                                <Typography variant="h6">Products</Typography>
                            </Box>
                            <Divider sx={{ mb: 2 }} />
                            <List dense>
                                {orderItems.map((item, idx) => (
                                    <ListItem key={idx} disablePadding>
                                        <ListItemText
                                            primary={`${item.product.title} x ${item.quantity}`}
                                            secondary={`${item.product.price?.toLocaleString()}₫ each`}
                                        />
                                    </ListItem>
                                ))}
                            </List>
                        </CardContent>
                    </Card>
                </Grid>

                {/* Payment Info */}
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Box display="flex" alignItems="center" mb={2}>
                                <Avatar sx={{ bgcolor: "success.main", mr: 2 }}>
                                    <CreditCardIcon />
                                </Avatar>
                                <Typography variant="h6">Transaction</Typography>
                            </Box>
                            <Divider sx={{ mb: 2 }} />
                            <InfoRow label="Transaction ID" value={transaction?.transactionId} />
                            <InfoRow label="Transaction No" value={transaction?.transactionNo} />
                            <InfoRow label="Amount" value={`${transaction?.amount?.toLocaleString()}₫`} />
                            <InfoRow label="Paid At" value={transaction?.datetime} />
                            <InfoRow label="Status" value="Success" />
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Container>
    );
};

export default PaymentHistory;
