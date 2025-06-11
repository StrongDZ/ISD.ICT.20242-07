import React, { useEffect, useState } from "react";
import { Container, Typography, Box, Alert } from "@mui/material";
import { useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import OrderSuccess from "../../components/Order/OrderSuccess";
import LoadingSpinner from "../../components/Common/LoadingSpinner";
import axios from "axios"; // Import axios ở đây

const OrderSuccessPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();
    const [orderData, setOrderData] = useState(null);
    const [paymentUrl, setPaymentUrl] = useState(""); // Khởi tạo là chuỗi rỗng
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!isAuthenticated()) {
            navigate("/login");
            return;
        }

        const getOrderData = () => {
            try {
                const stateOrderData = location.state?.orderData;
                if (stateOrderData) {
                    setOrderData(stateOrderData);
                    return stateOrderData;
                }

                const orderId = location.state?.orderId;
                const savedOrders = JSON.parse(localStorage.getItem("userOrders") || "[]");

                if (orderId) {
                    const foundOrder = savedOrders.find((order) => order.id === orderId);
                    if (foundOrder) {
                        setOrderData(foundOrder);
                        return foundOrder;
                    }
                }

                if (savedOrders.length > 0) {
                    const latestOrder = savedOrders[savedOrders.length - 1];
                    setOrderData(latestOrder);
                    return latestOrder;
                }

                setError("No order information found. Please check your order history.");
                return null;
            } catch (err) {
                console.error("Error loading order data:", err);
                setError("Failed to load order information.");
                return null;
            } finally {
                setLoading(false);
            }
        };

        const initialOrder = getOrderData();

        const fetchPaymentUrl = async (orderId) => {
            if (!orderId) return;

            try {
                const res = await axios.get(`http://localhost:8080/api/payments/url`, {
                    params: { orderId: "ORD004" }
                });
                // --- THÊM DÒNG NÀY ĐỂ IN URL RA CONSOLE ---
                console.log("VNPay Payment URL:", res.data);
                // ------------------------------------------

                setPaymentUrl(res.data);
            } catch (error) {
                console.error("Error fetching VNPay URL:", error);
            }
        };

        if (initialOrder?.id) {
            fetchPaymentUrl(initialOrder.id);
        } else {
            setLoading(false);
        }

    }, [isAuthenticated, location.state, navigate]);


    const handleContinueShopping = () => {
        navigate("/products");
    };

    const handleViewOrders = () => {
        navigate("/", { state: { message: "Order history feature coming soon!" } });
    };

    if (loading) {
        return <LoadingSpinner message="Loading order information..." />;
    }

    if (error) {
        return (
            <Container maxWidth="md" sx={{ py: 4 }}>
                <Alert severity="error" sx={{ mb: 4 }}>
                    <Typography variant="h6" gutterBottom>
                        Error Loading Order
                    </Typography>
                    <Typography variant="body2">{error}</Typography>
                </Alert>
                <Box sx={{ textAlign: "center" }}>
                    <Typography variant="body1" color="text.secondary" gutterBottom>
                        You can try:
                    </Typography>
                    <Box sx={{ display: "flex", gap: 2, justifyContent: "center", mt: 2 }}>
                        <button
                            onClick={() => navigate("/cart")}
                            style={{
                                padding: "8px 16px",
                                backgroundColor: "#1976d2",
                                color: "white",
                                border: "none",
                                borderRadius: "4px",
                                cursor: "pointer",
                            }}
                        >
                            Go to Cart
                        </button>
                        <button
                            onClick={() => navigate("/products")}
                            style={{
                                padding: "8px 16px",
                                backgroundColor: "#1976d2",
                                color: "white",
                                border: "none",
                                borderRadius: "4px",
                                cursor: "pointer",
                            }}
                        >
                            Continue Shopping
                        </button>
                    </Box>
                </Box>
            </Container>
        );
    }

    return (
        <Container maxWidth="md" sx={{ py: 4 }}>
            {/* Truyền paymentUrl xuống OrderSuccess */}
            <OrderSuccess
                order={orderData}
                paymentUrl={paymentUrl} // Truyền paymentUrl vào đây
                onContinueShopping={handleContinueShopping}
                onViewOrders={handleViewOrders}
            />
        </Container>
    );
};

export default OrderSuccessPage;