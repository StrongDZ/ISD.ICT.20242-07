import React, { useEffect, useState } from "react";
import axios from "axios"; // Vẫn giữ axios nếu bạn có ý định dùng nó ở các component khác hoặc trong tương lai.
import {
    Card, CardContent, Typography, Box, Button, Chip, Alert
} from "@mui/material";
import {
    CheckCircle, LocalShipping, Inventory, Phone,
    Home, ShoppingCart, Receipt
} from "@mui/icons-material";
import {
    Timeline, TimelineItem, TimelineOppositeContent,
    TimelineSeparator, TimelineDot, TimelineConnector, TimelineContent
} from "@mui/lab";

// Thêm paymentUrl vào props nhận vào
const OrderSuccess = ({ order, paymentUrl, onContinueShopping, onViewOrders }) => {
    // ⚠️ Xóa dòng này: const [setPaymentUrl] = useState(null);
    // ⚠️ Xóa luôn khối useEffect để gọi API VNPay ở đây

    const formatPrice = (price) => new Intl.NumberFormat("vi-VN", {
        style: "currency", currency: "VND",
    }).format(price);

    const formatDate = (dateString) => new Date(dateString).toLocaleString("vi-VN");

    const getDeliverySteps = () => {
        const isRush = order?.deliveryInfo?.isRushOrder;
        return isRush ? [
            {
                time: "Now", title: "Order Confirmed", description: "We've received your order and are preparing it",
                icon: <CheckCircle color="success" />, completed: true,
            },
            {
                time: "Within 2 hours", title: "Out for Delivery", description: "Your order is on the way",
                icon: <LocalShipping color="primary" />, completed: false,
            },
            {
                time: "Same day", title: "Delivered", description: "Order delivered to your address",
                icon: <Home color="primary" />, completed: false,
            }
        ] : [
            {
                time: "Now", title: "Order Confirmed", description: "We've received your order and are preparing it",
                icon: <CheckCircle color="success" />, completed: true,
            },
            {
                time: "1-2 days", title: "Processing", description: "Your order is being prepared for shipment",
                icon: <Inventory color="primary" />, completed: false,
            },
            {
                time: "2-3 days", title: "Shipped", description: "Your order is on the way",
                icon: <LocalShipping color="primary" />, completed: false,
            },
            {
                time: "3-5 days", title: "Delivered", description: "Order delivered to your address",
                icon: <Home color="primary" />, completed: false,
            }
        ];
    };

    const deliverySteps = getDeliverySteps();

    // 🔗 Dòng này sẽ được bỏ vì OrderSuccessPage sẽ fetch URL
    // useEffect(() => {
    //     const fetchPaymentUrl = async () => {
    //         try {
    //             const res = await axios.get(`http://localhost:8080/api/payments/url`, {
    //                 params: { orderId: order?.id }
    //             });
    //             setPaymentUrl(res.data); // Giả sử trả về plain URL string
    //         } catch (error) {
    //             console.error("Error fetching VNPay URL:", error);
    //         }
    //     };

    //     if (order?.id) {
    //         fetchPaymentUrl();
    //     }
    // }, [order?.id]);

    return (
        <Box>
            {/* Success Header */}
            <Card sx={{ mb: 4, textAlign: "center", bgcolor: "success.50" }}>
                <CardContent sx={{ py: 6 }}>
                    <CheckCircle sx={{ fontSize: 80, color: "success.main", mb: 2 }} />
                    <Typography variant="h4" gutterBottom sx={{ fontWeight: "bold" }}>
                        Order Placed Successfully!
                    </Typography>
                    <Typography variant="h6" color="text.secondary" gutterBottom>
                        Order ID: {order?.id}
                    </Typography>
                    <Typography variant="body1" color="text.secondary">
                        Thank you for your purchase. We'll send you a confirmation email shortly.
                    </Typography>
                </CardContent>
            </Card>

            {/* Order Details */}
            <Card sx={{ mb: 4 }}>
                <CardContent>
                    <Typography variant="h6" gutterBottom>
                        Order Details
                    </Typography>
                    <Box sx={{ display: "flex", flexWrap: "wrap", gap: 2, mb: 3 }}>
                        <Chip icon={<Receipt />} label={`${order?.items?.length || 0} items`} color="primary" variant="outlined" />
                        <Chip icon={<LocalShipping />} label={order?.deliveryInfo?.isRushOrder ? "Rush Delivery" : "Standard Delivery"} color={order?.deliveryInfo?.isRushOrder ? "success" : "info"} />
                        <Chip label={`Total: ${formatPrice(order?.total || 0)}`} color="secondary" />
                    </Box>

                    {order?.deliveryInfo && (
                        <Box sx={{ p: 2, bgcolor: "grey.50", borderRadius: 1 }}>
                            <Typography variant="subtitle1" gutterBottom sx={{ fontWeight: "medium" }}>
                                Delivery Information
                            </Typography>
                            <Typography variant="body2"><strong>Recipient:</strong> {order.deliveryInfo.recipientName}</Typography>
                            <Typography variant="body2"><strong>Phone:</strong> {order.deliveryInfo.phoneNumber}</Typography>
                            <Typography variant="body2"><strong>Address:</strong> {order.deliveryInfo.addressDetail}, {order.deliveryInfo.district}, {order.deliveryInfo.city}</Typography>
                            <Typography variant="body2"><strong>Order Date:</strong> {formatDate(order.date)}</Typography>
                        </Box>
                    )}
                </CardContent>
            </Card>

            {/* Hoàn tất thanh toán - Chỉ hiển thị khi paymentUrl có giá trị */}
            {paymentUrl && (
                <Box textAlign="center" my={3}>
                    <Typography variant="h6" gutterBottom>
                        Hoàn tất thanh toán
                    </Typography>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                        Vui lòng nhấn nút dưới đây để chuyển sang cổng thanh toán VNPay
                    </Typography>
                    <Button
                        variant="contained"
                        color="success"
                        size="large"
                        onClick={() => window.open(paymentUrl, "_blank")}
                    >
                        Thanh toán qua VNPay
                    </Button>
                </Box>
            )}


            {/* Delivery Timeline */}
            <Card sx={{ mb: 4 }}>
                <CardContent>
                    <Typography variant="h6" gutterBottom>
                        Delivery Progress
                    </Typography>
                    <Timeline position="left">
                        {deliverySteps.map((step, index) => (
                            <TimelineItem key={index}>
                                <TimelineOppositeContent sx={{ m: "auto 0" }} align="right" variant="body2" color="text.secondary">
                                    {step.time}
                                </TimelineOppositeContent>
                                <TimelineSeparator>
                                    <TimelineDot sx={{ bgcolor: "transparent", boxShadow: "none" }}>{step.icon}</TimelineDot>
                                    {index < deliverySteps.length - 1 && <TimelineConnector />}
                                </TimelineSeparator>
                                <TimelineContent sx={{ py: "12px", px: 2 }}>
                                    <Typography variant="h6" component="span" sx={{ fontWeight: step.completed ? "bold" : "normal" }}>
                                        {step.title}
                                    </Typography>
                                    <Typography color="text.secondary">{step.description}</Typography>
                                </TimelineContent>
                            </TimelineItem>
                        ))}
                    </Timeline>
                </CardContent>
            </Card>

            {/* Payment Info */}
            {paymentUrl && ( // Chỉ hiển thị Alert khi có paymentUrl
                <Alert severity="info" sx={{ mb: 4 }}>
                    <Typography variant="body2">
                        <strong>Important:</strong> This is a VNPay order. Please complete the payment via VNPay by clicking the button below.
                    </Typography>
                    <Box mt={2}>
                        <Button variant="contained" color="success" onClick={() => window.open(paymentUrl, "_blank")}>
                            Pay via VNPay
                        </Button>
                    </Box>
                </Alert>
            )}


            {/* What's Next? */}
            <Card sx={{ mb: 4 }}>
                <CardContent>
                    <Typography variant="h6" gutterBottom>What's Next?</Typography>
                    <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
                        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                            <CheckCircle color="success" />
                            <Typography variant="body2">You'll receive a confirmation message shortly</Typography>
                        </Box>
                        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                            <Phone color="primary" />
                            <Typography variant="body2">Our team may call you to confirm delivery details</Typography>
                        </Box>
                        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                            <LocalShipping color="primary" />
                            <Typography variant="body2">You'll be notified when your order ships</Typography>
                        </Box>
                        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                            <Home color="primary" />
                            <Typography variant="body2">Be available at the delivery address during the estimated time</Typography>
                        </Box>
                    </Box>
                </CardContent>
            </Card>

            {/* Buttons */}
            <Box sx={{ display: "flex", gap: 2, justifyContent: "center", flexWrap: "wrap" }}>
                <Button variant="contained" startIcon={<ShoppingCart />} onClick={onContinueShopping} size="large">
                    Continue Shopping
                </Button>
                {onViewOrders && (
                    <Button variant="outlined" startIcon={<Receipt />} onClick={onViewOrders} size="large">
                        View My Orders
                    </Button>
                )}
            </Box>
        </Box>
    );
};

export default OrderSuccess;