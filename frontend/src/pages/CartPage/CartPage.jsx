import React, { useEffect, useState } from "react";
import {
    Container,
    Typography,
    Box,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    IconButton,
    Button,
    TextField,
    Divider,
    Card,
    CardContent,
    Grid,
    Alert,
    Chip,
    Tooltip,
    Snackbar,
} from "@mui/material";
import { Add, Remove, Delete, ShoppingCart, ArrowForward, Warning, Info } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { useCart } from "../../contexts/CartContext";

const CartPage = () => {
    const navigate = useNavigate();
    const {
        cartItems,
        updateCartItem,
        removeFromCart,
        clearCart,
        getCartTotal,
        getCartCount,
        getTotalExcludingVAT,
        getVATAmount,
        hasInventoryIssues,
    } = useCart();
    const [errorMessage, setErrorMessage] = useState("");
    const [showError, setShowError] = useState(false);

    const formatPrice = (price) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(price);
    };

    // Handle error display
    const handleError = (error) => {
        setErrorMessage(error.message || "Có lỗi xảy ra");
        setShowError(true);
    };

    const handleCloseError = () => {
        setShowError(false);
        setErrorMessage("");
    };

    // Handle quantity update with error handling
    const handleQuantityUpdate = async (product, newQuantity) => {
        try {
            await updateCartItem(product, newQuantity);
        } catch (error) {
            handleError(error);
        }
    };

    // Check inventory status for each item
    const getInventoryStatus = (item) => {
        if (!item || !item.productDTO) return { status: "unknown", message: "", shortfall: 0 };

        const product = item.productDTO;
        const availableStock = product.quantity || 0;
        const requestedQuantity = item.quantity || 0;

        if (availableStock === 0) {
            return {
                status: "out-of-stock",
                message: "Sản phẩm hiện không có sẵn",
                shortfall: requestedQuantity,
            };
        } else if (requestedQuantity > availableStock) {
            const shortfall = requestedQuantity - availableStock;
            return {
                status: "insufficient",
                message: `Chỉ còn ${availableStock} sản phẩm trong kho (thiếu ${shortfall})`,
                shortfall: shortfall,
            };
        } else if (availableStock <= 5) {
            return {
                status: "low-stock",
                message: `Chỉ còn ${availableStock} sản phẩm trong kho`,
                shortfall: 0,
            };
        } else {
            return {
                status: "available",
                message: `${availableStock} sản phẩm có sẵn`,
                shortfall: 0,
            };
        }
    };

    // Get inventory status color
    const getStatusColor = (status) => {
        switch (status) {
            case "out-of-stock":
                return "error";
            case "insufficient":
                return "warning";
            case "low-stock":
                return "warning";
            case "available":
                return "success";
            default:
                return "default";
        }
    };

    // Get total shortfall across all items
    const getTotalShortfall = () => {
        return cartItems.reduce((total, item) => {
            const status = getInventoryStatus(item);
            return total + status.shortfall;
        }, 0);
    };

    const handleRemoveItem = (product) => {
        removeFromCart(product);
    };

    const handleClearCart = () => {
        if (window.confirm("Bạn có chắc chắn muốn xóa toàn bộ giỏ hàng?")) {
            clearCart();
        }
    };

    const handleContinueShopping = () => {
        navigate("/products");
    };

    const handleCheckout = () => {
        if (hasInventoryIssues()) {
            alert("Vui lòng kiểm tra lại số lượng sản phẩm trước khi thanh toán!");
            return;
        }
        navigate("/checkout");
    };

    if (cartItems.length === 0) {
        return (
            <Container maxWidth="lg" sx={{ py: 4 }}>
                <Paper sx={{ p: 4, textAlign: "center" }}>
                    <ShoppingCart sx={{ fontSize: 80, color: "grey.400", mb: 2 }} />
                    <Typography variant="h4" gutterBottom>
                        Giỏ hàng trống
                    </Typography>
                    <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
                        Bạn chưa thêm sản phẩm nào vào giỏ hàng.
                    </Typography>
                    <Button variant="contained" size="large" onClick={handleContinueShopping} startIcon={<ShoppingCart />}>
                        Bắt đầu mua sắm
                    </Button>
                </Paper>
            </Container>
        );
    }

    return (
        <>
            <Container maxWidth="lg" sx={{ py: 4 }}>
                <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: "bold" }}>
                    Giỏ hàng
                </Typography>

                {/* Inventory Warning Alert */}
                {hasInventoryIssues() && (
                    <Alert severity="warning" sx={{ mb: 3 }} icon={<Warning />}>
                        <Typography variant="body1" sx={{ fontWeight: "medium" }}>
                            Cảnh báo: Một số sản phẩm không đủ số lượng trong kho!
                        </Typography>
                        <Typography variant="body2" sx={{ mt: 1 }}>
                            Tổng cộng thiếu {getTotalShortfall()} sản phẩm. Vui lòng điều chỉnh số lượng hoặc xóa sản phẩm không có sẵn.
                        </Typography>
                    </Alert>
                )}

                <Grid container spacing={3}>
                    {/* Cart Items */}
                    <Grid item xs={12} md={8}>
                        <Paper sx={{ mb: 3 }}>
                            <Box sx={{ p: 2, display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                                <Typography variant="h6">
                                    Sản phẩm trong giỏ ({getCartCount()} {getCartCount() === 1 ? "sản phẩm" : "sản phẩm"})
                                </Typography>
                                <Button variant="outlined" color="error" startIcon={<Delete />} onClick={handleClearCart} size="small">
                                    Xóa giỏ hàng
                                </Button>
                            </Box>

                            <TableContainer>
                                <Table>
                                    <TableHead>
                                        <TableRow>
                                            <TableCell>Sản phẩm</TableCell>
                                            <TableCell align="center">Số lượng</TableCell>
                                            <TableCell align="right">Đơn giá</TableCell>
                                            <TableCell align="right">Thành tiền</TableCell>
                                            <TableCell align="center">Thao tác</TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {cartItems.map((item) => {
                                            if (!item || !item.productDTO) {
                                                return null;
                                            }

                                            const product = item.productDTO;
                                            const inventoryStatus = getInventoryStatus(item);

                                            return (
                                                <TableRow key={product.productID}>
                                                    <TableCell>
                                                        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                                                            <img
                                                                src={product.imageURL}
                                                                alt={product.title}
                                                                style={{
                                                                    width: 60,
                                                                    height: 60,
                                                                    objectFit: "cover",
                                                                    borderRadius: 4,
                                                                }}
                                                            />
                                                            <Box sx={{ flex: 1 }}>
                                                                <Typography variant="subtitle1" sx={{ fontWeight: "medium" }}>
                                                                    {product.title}
                                                                </Typography>
                                                                <Typography variant="body2" color="text.secondary">
                                                                    ID: {product.productID} | Loại: {product.category}
                                                                </Typography>
                                                                <Box sx={{ mt: 1 }}>
                                                                    <Chip
                                                                        label={inventoryStatus.message}
                                                                        color={getStatusColor(inventoryStatus.status)}
                                                                        size="small"
                                                                        icon={inventoryStatus.status === "available" ? <Info /> : <Warning />}
                                                                    />
                                                                </Box>
                                                            </Box>
                                                        </Box>
                                                    </TableCell>

                                                    <TableCell align="center">
                                                        <Box sx={{ display: "flex", alignItems: "center", gap: 1, justifyContent: "center" }}>
                                                            <IconButton
                                                                size="small"
                                                                onClick={() => handleQuantityUpdate(product, item.quantity - 1)}
                                                                disabled={item.quantity <= 1}
                                                            >
                                                                <Remove />
                                                            </IconButton>
                                                            <TextField
                                                                size="small"
                                                                value={item.quantity}
                                                                onChange={(e) => {
                                                                    const value = parseInt(e.target.value);
                                                                    if (!isNaN(value) && value > 0) {
                                                                        handleQuantityUpdate(product, value);
                                                                    }
                                                                }}
                                                                inputProps={{
                                                                    style: { textAlign: "center", width: "50px" },
                                                                    min: 1,
                                                                    max: product.quantity || 999,
                                                                }}
                                                                error={
                                                                    inventoryStatus.status === "out-of-stock" ||
                                                                    inventoryStatus.status === "insufficient"
                                                                }
                                                                helperText={
                                                                    inventoryStatus.status === "insufficient" ? `Tối đa: ${product.quantity}` : ""
                                                                }
                                                            />
                                                            <IconButton
                                                                size="small"
                                                                onClick={() => handleQuantityUpdate(product, item.quantity + 1)}
                                                                disabled={
                                                                    inventoryStatus.status === "out-of-stock" ||
                                                                    (product.quantity && item.quantity >= product.quantity)
                                                                }
                                                            >
                                                                <Add />
                                                            </IconButton>
                                                        </Box>
                                                    </TableCell>

                                                    <TableCell align="right">
                                                        <Typography variant="body1">{formatPrice(product.price)}</Typography>
                                                    </TableCell>

                                                    <TableCell align="right">
                                                        <Typography variant="subtitle1" sx={{ fontWeight: "bold" }}>
                                                            {formatPrice(product.price * item.quantity)}
                                                        </Typography>
                                                    </TableCell>

                                                    <TableCell align="center">
                                                        <Tooltip title="Xóa sản phẩm">
                                                            <IconButton color="error" onClick={() => handleRemoveItem(product)}>
                                                                <Delete />
                                                            </IconButton>
                                                        </Tooltip>
                                                    </TableCell>
                                                </TableRow>
                                            );
                                        })}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        </Paper>
                    </Grid>

                    {/* Order Summary */}
                    <Grid item xs={12} md={4}>
                        <Card sx={{ position: "sticky", top: 24 }}>
                            <CardContent>
                                <Typography variant="h6" gutterBottom>
                                    Tóm tắt đơn hàng
                                </Typography>

                                <Box sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}>
                                    <Typography variant="body1">Tạm tính (chưa VAT):</Typography>
                                    <Typography variant="body1">{formatPrice(getTotalExcludingVAT())}</Typography>
                                </Box>

                                <Box sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}>
                                    <Typography variant="body1">VAT (10%):</Typography>
                                    <Typography variant="body1">{formatPrice(getVATAmount())}</Typography>
                                </Box>

                                <Divider sx={{ my: 2 }} />

                                <Box sx={{ display: "flex", justifyContent: "space-between", mb: 3 }}>
                                    <Typography variant="h6" sx={{ fontWeight: "bold" }}>
                                        Tổng cộng:
                                    </Typography>
                                    <Typography variant="h6" sx={{ fontWeight: "bold", color: "primary.main" }}>
                                        {formatPrice(getCartTotal())}
                                    </Typography>
                                </Box>

                                <Button
                                    variant="contained"
                                    fullWidth
                                    size="large"
                                    onClick={handleCheckout}
                                    endIcon={<ArrowForward />}
                                    sx={{ mb: 2 }}
                                    disabled={hasInventoryIssues()}
                                >
                                    Tiến hành thanh toán
                                </Button>

                                <Button variant="outlined" fullWidth onClick={handleContinueShopping}>
                                    Tiếp tục mua sắm
                                </Button>

                                <Box sx={{ mt: 3, p: 2, backgroundColor: "grey.50", borderRadius: 1 }}>
                                    <Typography variant="body2" color="text.secondary" sx={{ textAlign: "center" }}>
                                        <strong>Miễn phí vận chuyển</strong> cho đơn hàng trên 100,000 VND
                                        <br />
                                        (giảm tối đa 25,000 VND)
                                    </Typography>
                                </Box>
                            </CardContent>
                        </Card>
                    </Grid>
                </Grid>
            </Container>

            {/* Error Snackbar */}
            <Snackbar open={showError} autoHideDuration={6000} onClose={handleCloseError} anchorOrigin={{ vertical: "top", horizontal: "center" }}>
                <Alert onClose={handleCloseError} severity="error" sx={{ width: "100%" }}>
                    {errorMessage}
                </Alert>
            </Snackbar>
        </>
    );
};

export default CartPage;
