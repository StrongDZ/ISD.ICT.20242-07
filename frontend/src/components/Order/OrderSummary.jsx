import React from "react";
import { Card, CardContent, Typography, Divider, Box, List, ListItem, ListItemText, ListItemAvatar, Avatar, Chip } from "@mui/material";
import { ShoppingCart } from "@mui/icons-material";

const OrderSummary = ({ order, items = [], showTitle = true, compact = false }) => {
    const formatPrice = (price) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(price);
    };

    const calculateSubtotal = () => {
        return items.reduce((total, item) => total + item.price * item.quantity, 0);
    };

    const subtotal = order?.subtotal || calculateSubtotal();
    const deliveryFee = order?.deliveryFee || 50000;
    const vat = order?.vat || subtotal * 0.1;
    const total = order?.total || subtotal + deliveryFee + vat;

    return (
        <Card>
            <CardContent>
                {showTitle && (
                    <Typography variant="h6" gutterBottom>
                        Order Summary
                    </Typography>
                )}

                {/* Order Items */}
                {items.length > 0 && (
                    <List dense={compact}>
                        {items.map((item) => (
                            <ListItem key={item.productID} sx={{ px: 0 }}>
                                <ListItemAvatar>
                                    <Avatar src={item.imageURL} alt={item.title} sx={{ width: compact ? 40 : 56, height: compact ? 40 : 56 }} />
                                </ListItemAvatar>
                                <ListItemText
                                    primary={
                                        <Typography variant={compact ? "body2" : "body1"} noWrap>
                                            {item.title}
                                        </Typography>
                                    }
                                    secondary={
                                        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                                            <Typography variant="body2" color="text.secondary">
                                                {formatPrice(item.price)} × {item.quantity}
                                            </Typography>
                                            {item.category && <Chip label={item.category.toUpperCase()} size="small" variant="outlined" />}
                                        </Box>
                                    }
                                />
                                <Typography variant={compact ? "body2" : "body1"} sx={{ fontWeight: "bold" }}>
                                    {formatPrice(item.price * item.quantity)}
                                </Typography>
                            </ListItem>
                        ))}
                    </List>
                )}

                <Divider sx={{ my: 2 }} />

                {/* Price Breakdown */}
                <Box sx={{ space: 1 }}>
                    <Box sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}>
                        <Typography variant="body2">Subtotal:</Typography>
                        <Typography variant="body2">{formatPrice(subtotal)}</Typography>
                    </Box>

                    <Box sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}>
                        <Typography variant="body2">Delivery Fee:</Typography>
                        <Typography variant="body2">{formatPrice(deliveryFee)}</Typography>
                    </Box>

                    <Box sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}>
                        <Typography variant="body2">VAT (10%):</Typography>
                        <Typography variant="body2">{formatPrice(vat)}</Typography>
                    </Box>

                    <Divider sx={{ my: 1 }} />

                    <Box sx={{ display: "flex", justifyContent: "space-between" }}>
                        <Typography variant="h6" sx={{ fontWeight: "bold" }}>
                            Total:
                        </Typography>
                        <Typography variant="h6" sx={{ fontWeight: "bold", color: "primary.main" }}>
                            {formatPrice(total)}
                        </Typography>
                    </Box>
                </Box>

                {/* Order Info */}
                {order && (
                    <Box sx={{ mt: 2, pt: 2, borderTop: 1, borderColor: "divider" }}>
                        <Typography variant="body2" color="text.secondary" gutterBottom>
                            Order ID: {order.id || order.orderID}
                        </Typography>
                        {order.date && (
                            <Typography variant="body2" color="text.secondary">
                                Order Date: {new Date(order.date).toLocaleDateString("vi-VN")}
                            </Typography>
                        )}
                    </Box>
                )}
            </CardContent>
        </Card>
    );
};

export default OrderSummary;
