import React, { useEffect, useState } from "react";
import { Container, Typography, Box, Alert } from "@mui/material";
import { useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import OrderSuccess from "../../components/Order/OrderSuccess";
import LoadingSpinner from "../../components/Common/LoadingSpinner";

const OrderSuccessPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();
    const [orderData, setOrderData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        // Check authentication
        if (!isAuthenticated()) {
            navigate("/login");
            return;
        }

        // Get order data from navigation state or localStorage
        const getOrderData = () => {
            try {
                // First try to get from navigation state
                const stateOrderData = location.state?.orderData;
                if (stateOrderData) {
                    setOrderData(stateOrderData);
                    setLoading(false);
                    return;
                }

                // If no state data, try to get the latest order from localStorage
                const orderId = location.state?.orderId;
                const savedOrders = JSON.parse(localStorage.getItem("userOrders") || "[]");

                if (orderId) {
                    const foundOrder = savedOrders.find((order) => order.id === orderId);
                    if (foundOrder) {
                        setOrderData(foundOrder);
                        setLoading(false);
                        return;
                    }
                }

                // Get the most recent order if no specific order ID
                if (savedOrders.length > 0) {
                    const latestOrder = savedOrders[savedOrders.length - 1];
                    setOrderData(latestOrder);
                    setLoading(false);
                    return;
                }

                // No order found
                setError("No order information found. Please check your order history.");
                setLoading(false);
            } catch (err) {
                console.error("Error loading order data:", err);
                setError("Failed to load order information.");
                setLoading(false);
            }
        };

        getOrderData();
    }, [isAuthenticated, location.state, navigate]);

    const handleContinueShopping = () => {
        navigate("/products");
    };

    const handleViewOrders = () => {
        // For now, redirect to home. In a real app, this would go to orders page
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
            <OrderSuccess order={orderData} onContinueShopping={handleContinueShopping} onViewOrders={handleViewOrders} />
        </Container>
    );
};

export default OrderSuccessPage;
