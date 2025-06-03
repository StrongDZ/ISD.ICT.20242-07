import React, { useState } from "react";
import { Card, CardMedia, CardContent, CardActions, Typography, Button, Box, Chip, IconButton, Snackbar, Alert } from "@mui/material";
import { ShoppingCart, Visibility } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { useCart } from "../../contexts/CartContext";

const ProductCard = ({ product }) => {
    const navigate = useNavigate();
    const { addToCart } = useCart();
    const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });

    const formatPrice = (price) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(price);
    };

    const getCategoryColor = (category) => {
        switch (category?.toLowerCase()) {
            case "book":
                return "primary";
            case "cd":
                return "warning";
            case "dvd":
                return "secondary";
            default:
                return "default";
        }
    };

    const handleViewDetails = () => {
        navigate(`/products/${product.productID}`);
    };

    const handleAddToCart = async (e) => {
        e.stopPropagation();
        try {
            await addToCart(product, 1);
            setSnackbar({
                open: true,
                message: "Product added to cart successfully!",
                severity: "success",
            });
        } catch (error) {
            setSnackbar({
                open: true,
                message: "Failed to add product to cart",
                severity: "error",
            });
        }
    };

    const handleCloseSnackbar = () => {
        setSnackbar({ ...snackbar, open: false });
    };

    return (
        <>
            <Card
                sx={{
                    height: "100%",
                    display: "flex",
                    flexDirection: "column",
                    cursor: "pointer",
                    transition: "transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out",
                    "&:hover": {
                        transform: "translateY(-4px)",
                        boxShadow: 4,
                    },
                }}
                onClick={handleViewDetails}
            >
                <Box sx={{ position: "relative" }}>
                    <CardMedia component="img" height="250" image={product.imageURL} alt={product.title} sx={{ objectFit: "cover" }} />
                    <Chip
                        label={product.category?.toUpperCase()}
                        color={getCategoryColor(product.category)}
                        size="small"
                        sx={{
                            position: "absolute",
                            top: 8,
                            left: 8,
                            fontWeight: "bold",
                        }}
                    />
                    {product.quantity <= 5 && product.quantity > 0 && (
                        <Chip
                            label="Low Stock"
                            color="error"
                            size="small"
                            sx={{
                                position: "absolute",
                                top: 8,
                                right: 8,
                                fontWeight: "bold",
                            }}
                        />
                    )}
                    {product.quantity === 0 && (
                        <Chip
                            label="Out of Stock"
                            color="error"
                            size="small"
                            sx={{
                                position: "absolute",
                                top: 8,
                                right: 8,
                                fontWeight: "bold",
                            }}
                        />
                    )}
                </Box>

                <CardContent sx={{ flexGrow: 1, p: 2 }}>
                    <Typography
                        variant="h6"
                        component="h3"
                        gutterBottom
                        sx={{
                            fontWeight: "bold",
                            overflow: "hidden",
                            textOverflow: "ellipsis",
                            display: "-webkit-box",
                            WebkitLineClamp: 2,
                            WebkitBoxOrient: "vertical",
                            minHeight: "3em",
                        }}
                    >
                        {product.title}
                    </Typography>

                    {/* Category-specific information */}
                    {product.category === "book" && product.authors && (
                        <Typography variant="body2" color="text.secondary" gutterBottom>
                            by {product.authors}
                        </Typography>
                    )}
                    {product.category === "cd" && product.artist && (
                        <Typography variant="body2" color="text.secondary" gutterBottom>
                            by {product.artist}
                        </Typography>
                    )}
                    {product.category === "dvd" && product.director && (
                        <Typography variant="body2" color="text.secondary" gutterBottom>
                            Directed by {product.director}
                        </Typography>
                    )}

                    <Typography
                        variant="body2"
                        color="text.secondary"
                        sx={{
                            overflow: "hidden",
                            textOverflow: "ellipsis",
                            display: "-webkit-box",
                            WebkitLineClamp: 2,
                            WebkitBoxOrient: "vertical",
                            mb: 2,
                        }}
                    >
                        {product.description}
                    </Typography>

                    <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                        <Typography variant="h6" color="primary" sx={{ fontWeight: "bold" }}>
                            {formatPrice(product.price)}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            Stock: {product.quantity}
                        </Typography>
                    </Box>
                </CardContent>

                <CardActions sx={{ p: 2, pt: 0 }}>
                    <Button variant="outlined" startIcon={<Visibility />} onClick={handleViewDetails} sx={{ mr: 1 }}>
                        View Details
                    </Button>
                    <Button
                        variant="contained"
                        startIcon={<ShoppingCart />}
                        onClick={handleAddToCart}
                        disabled={product.quantity === 0}
                        sx={{ flexGrow: 1 }}
                    >
                        Add to Cart
                    </Button>
                </CardActions>
            </Card>

            <Snackbar
                open={snackbar.open}
                autoHideDuration={3000}
                onClose={handleCloseSnackbar}
                anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
            >
                <Alert onClose={handleCloseSnackbar} severity={snackbar.severity}>
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </>
    );
};

export default ProductCard;
