import React from "react";
import { Card, CardContent, Typography, Divider, Box, List, ListItem, ListItemText, ListItemAvatar, Avatar, Chip } from "@mui/material";
import { ShoppingCart } from "@mui/icons-material";

const OrderSummary = ({ order, items = [], showTitle = true, compact = false, deliveryInfo = null }) => {
    const formatPrice = (price) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(price);
    };

    const calculateSubtotal = () => {
        return items.reduce((total, item) => total + item.product?.price * item.quantity, 0);
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
                            <ListItem key={item.product?.productID} sx={{ px: 0 }}>
                                <ListItemAvatar>
                                    <Avatar
                                        src={item.product?.imageURL}
                                        alt={item.product?.title}
                                        sx={{ width: compact ? 40 : 56, height: compact ? 40 : 56 }}
                                    />
                                </ListItemAvatar>
                                <ListItemText
                                    primary={
                                        <Typography variant={compact ? "body2" : "body1"} noWrap>
                                            {item.product?.title}
                                        </Typography>
                                    }
                                    secondary={
                                        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                                            <Typography variant="body2" color="text.secondary">
                                                {formatPrice(item.product?.price)} × {item.quantity}
                                            </Typography>
                                            {item.product?.category && (
                                                <Chip label={item.product.category.toUpperCase()} size="small" variant="outlined" />
                                            )}
                                        </Box>
                                    }
                                />
                                <Typography variant={compact ? "body2" : "body1"} sx={{ fontWeight: "bold" }}>
                                    {formatPrice((item.product?.price || 0) * item.quantity)}
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

                {/* Rush Delivery Information */}
                {deliveryInfo && deliveryInfo.isRushOrder && (
                    <Box sx={{ mt: 2, p: 2, bgcolor: "success.light", borderRadius: 1 }}>
                        <Typography variant="subtitle2" gutterBottom sx={{ color: "success.dark" }}>
                            🚀 Rush Delivery Selected
                        </Typography>
                        <Typography variant="body2" sx={{ color: "success.dark", mb: 1 }}>
                            Same day delivery service
                        </Typography>
                        {deliveryInfo.deliveryTime && (
                            <Typography variant="body2" sx={{ color: "success.dark" }}>
                                Preferred time: {deliveryInfo.deliveryTime}
                            </Typography>
                        )}
                        {deliveryInfo.specialInstructions && (
                            <Typography variant="body2" sx={{ color: "success.dark" }}>
                                Special instructions: {deliveryInfo.specialInstructions}
                            </Typography>
                        )}
                    </Box>
                )}

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
