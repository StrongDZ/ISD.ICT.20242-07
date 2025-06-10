import React, { useEffect, useState } from "react";
import axios from "axios";
import { useLocation, useNavigate } from "react-router-dom"; // Thêm useLocation và useNavigate
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
    CircularProgress,
    Alert,
} from "@mui/material";
import ReceiptIcon from "@mui/icons-material/Receipt";
import CreditCardIcon from "@mui/icons-material/CreditCard";

// Component con giữ nguyên
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

// Sửa đổi component PaymentSuccess để lấy orderId trực tiếp
const PaymentSuccess = () => { // Bỏ orderId khỏi props
    const [order, setOrder] = useState(null);
    const [payment, setPayment] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const location = useLocation(); // Lấy đối tượng location từ react-router-dom
    const navigate = useNavigate(); // Sử dụng useNavigate để điều hướng

    // URL cơ sở của API backend của bạn
    const BASE_API_URL = "http://localhost:8080/api/payments"; // Thay thế bằng địa chỉ backend thực tế của bạn

    useEffect(() => {
        // Lấy orderId từ query parameters của URL
        const queryParams = new URLSearchParams(location.search);
        const orderId = queryParams.get('orderId'); // Lấy giá trị của tham số 'orderId'

        const fetchTransactionHistory = async () => {
            if (!orderId) {
                setError("Order ID is missing in the URL.");
                setLoading(false);
                return;
            }

            try {
                // Gọi API transaction_history
                const response = await axios.get(`${BASE_API_URL}/transaction_history`, {
                    params: {
                        orderId: orderId,
                    },
                });

                if (response.data && response.data.responseCode === 200) {
                    const transactionData = response.data.data;

                    setOrder({
                        id: transactionData.order.orderID,
                        customerName: transactionData.order.customerName,
                        customerPhoneNumber: transactionData.order.phoneNumber,
                        total: transactionData.order.totalAmount,
                        status: transactionData.order.status,
                        shippingAdress: transactionData.order.shippingAddress,
                        province: transactionData.order.province
                    });

                    setPayment({
                        transactionId: transactionData.transactionNo,
                        method: "VNPAY", // Giả sử phương thức thanh toán là VNPAY  
                        amount: transactionData.amount,
                        paidAt: transactionData.datetime,
                        status: "Success" // Giả sử trạng thái thanh toán là thành công,
                    });
                } else {
                    setError(response.data.message || "Failed to fetch transaction history.");
                }
            } catch (err) {
                console.error("Error fetching transaction history:", err);
                setError("An error occurred while fetching transaction details. Please try again.");
                if (err.response && err.response.data && err.response.data.message) {
                    setError(err.response.data.message);
                }
            } finally {
                setLoading(false);
            }
        };

        fetchTransactionHistory();
    }, [location.search]); // Dependency array: gọi lại khi chuỗi query parameters thay đổi

    // Hàm xử lý nút "Continue Shopping"
    const handleContinueShopping = () => {
        navigate('/products'); // Điều hướng về trang sản phẩm
    };

    // Hàm xử lý nút "View Orders"
    const handleViewOrders = () => {
        navigate('/my-orders'); // Điều hướng đến trang đơn hàng của người dùng (bạn có thể cần tạo route này)
    };

    if (loading) {
        return (
            <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", minHeight: "80vh" }}>
                <CircularProgress />
                <Typography variant="h6" sx={{ ml: 2 }}>Loading transaction details...</Typography>
            </Box>
        );
    }

    if (error) {
        return (
            <Container maxWidth="md" sx={{ mt: 6 }}>
                <Alert severity="error">
                    <Typography variant="h6">Error:</Typography>
                    <Typography>{error}</Typography>
                    <Button onClick={handleContinueShopping} sx={{ mt: 2 }}>
                        Back to Shopping
                    </Button>
                </Alert>
            </Container>
        );
    }

    if (!order || !payment) {
        return (
            <Container maxWidth="md" sx={{ mt: 6 }}>
                <Alert severity="warning">
                    <Typography variant="h6">No transaction details found.</Typography>
                    <Typography>The transaction ID might be invalid or no data was returned.</Typography>
                    <Button onClick={handleContinueShopping} sx={{ mt: 2 }}>
                        Back to Shopping
                    </Button>
                </Alert>
            </Container>
        );
    }


    return (
        <Box sx={{ textAlign: "center", mt: 6, mb: 4 }}>
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
                                {/* Sử dụng dữ liệu từ state */}
                                <InfoRow label="Order ID" value={order?.id || "N/A"} />
                                <InfoRow label="Customer" value={order?.customerName || "N/A"} />
                                <InfoRow label="Phone Number" value={order?.customerPhoneNumber || "N/A"} />
                                <InfoRow label="Total Amount" value={`${order?.total?.toLocaleString()}₫`} />
                                <InfoRow label="Status" value={order?.status || "N/A"} />
                                <InfoRow label="Shipping Address" value={order?.shippingAdress || "N/A"} />
                                <InfoRow label="Province" value={order?.province || "N/A"} />
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
                                {/* Sử dụng dữ liệu từ state */}
                                <InfoRow label="Transaction ID" value={payment?.transactionId || "N/A"} />
                                <InfoRow label="Method" value={payment?.method || "N/A"} />
                                <InfoRow label="Amount Paid" value={`${payment?.amount?.toLocaleString()}₫`} />
                                <InfoRow label="Paid At" value={payment?.paidAt || "N/A"} />
                                <InfoRow label="Status" value={payment?.status || "N/A"} />
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
                    onClick={handleContinueShopping} // Sử dụng hàm xử lý nội bộ
                    sx={{ px: 4 }}
                >
                    Continue Shopping
                </Button>
                <Button
                    variant="outlined"
                    color="secondary"
                    size="large"
                    onClick={handleViewOrders} // Sử dụng hàm xử lý nội bộ
                    sx={{ px: 4 }}
                >
                    View Orders
                </Button>
            </Box>
        </Box >
    );
};

export default PaymentSuccess;