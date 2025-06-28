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
    Checkbox,
} from "@mui/material";
import { Add, Remove, Delete, ShoppingCart, ArrowForward, Warning, Info } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { useCart } from "../../contexts/CartContext";
import { orderService } from "../../services/orderService";

const CartPage = () => {
    const navigate = useNavigate();
    const {
        cartItems,
        selectedItems,
        updateCartItem,
        removeFromCart,
        clearCart,
        getCartCount,
        hasInventoryIssues,
        loadCartItems,
        getInventoryStatus,
        validateStock,
        selectItem,
        unselectItem,
        selectAll,
        unselectAll,
        getSelectedItems,
        getSelectedCartTotal,
    } = useCart();
    const [errorMessage, setErrorMessage] = useState("");
    const [showError, setShowError] = useState(false);
    const [loading, setLoading] = useState(true);

    // Load cart items when component mounts and validate stock
    useEffect(() => {
        const loadCart = async () => {
            try {
                setLoading(true);
                await loadCartItems();

                // Validate stock after loading
                const validation = await validateStock(cartItems);

                if (!validation.isValid) {
                    setErrorMessage(`Một số sản phẩm trong giỏ hàng đã hết hàng hoặc không đủ số lượng. Giỏ hàng đã được cập nhật.`);
                    setShowError(true);
                }
            } catch (error) {
                console.error("Failed to load cart:", error);
                handleError(error);
            } finally {
                setLoading(false);
            }
        };

        loadCart();
    }, []);

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

    const handleCheckout = async () => {
        const selectedItemsList = getSelectedItems();

        if (selectedItemsList.length === 0) {
            alert("Vui lòng chọn ít nhất một sản phẩm để thanh toán!");
            return;
        }

        // Validate stock for selected items before checkout
        try {
            const validation = await validateStock(selectedItemsList);
            if (!validation.isValid) {
                alert("Một số sản phẩm đã hết hàng hoặc không đủ số lượng. Vui lòng kiểm tra lại giỏ hàng!");
                return;
            }

            navigate("/checkout");
        } catch (error) {
            handleError(error);
        }
    };

    // Handle checkbox changes
    const handleSelectItem = (productId, checked) => {
        if (checked) {
            selectItem(productId);
        } else {
            unselectItem(productId);
        }
    };

    const handleSelectAll = (checked) => {
        if (checked) {
            selectAll();
        } else {
            unselectAll();
        }
    };

    const isAllSelected = cartItems.length > 0 && cartItems.every((item) => selectedItems.has(item.productDTO?.productID));
    const isSomeSelected = cartItems.some((item) => selectedItems.has(item.productDTO?.productID));

    // Helper functions for selected items calculations
    const getSelectedTotalExcludingVAT = () => {
        return getSelectedCartTotal() / 1.1; // Assuming 10% VAT
    };

    const getSelectedVATAmount = () => {
        return getSelectedCartTotal() - getSelectedTotalExcludingVAT();
    };

    const getSelectedItemsCount = () => {
        return getSelectedItems().reduce((count, item) => count + item.quantity, 0);
    };

    if (loading) {
        return (
            <Container maxWidth="lg" sx={{ py: 4 }}>
                <Paper sx={{ p: 4, textAlign: "center" }}>
                    <Typography variant="h6" gutterBottom>
                        Đang tải giỏ hàng...
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                        Vui lòng chờ trong giây lát
                    </Typography>
                </Paper>
            </Container>
        );
    }

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
            <Container maxWidth="xl" sx={{ py: 4 }}>
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

                            <TableContainer sx={{ overflowX: "auto" }}>
                                <Table sx={{ minWidth: 800 }}>
                                    <TableHead>
                                        <TableRow>
                                            <TableCell padding="checkbox" sx={{ width: "50px" }}>
                                                <Checkbox
                                                    indeterminate={isSomeSelected && !isAllSelected}
                                                    checked={isAllSelected}
                                                    onChange={(e) => handleSelectAll(e.target.checked)}
                                                />
                                            </TableCell>
                                            <TableCell sx={{ minWidth: "300px" }}>Sản phẩm</TableCell>
                                            <TableCell align="center" sx={{ minWidth: "180px" }}>
                                                Số lượng
                                            </TableCell>
                                            <TableCell align="right" sx={{ minWidth: "120px" }}>
                                                Đơn giá
                                            </TableCell>
                                            <TableCell align="right" sx={{ minWidth: "140px" }}>
                                                Thành tiền
                                            </TableCell>
                                            <TableCell align="center" sx={{ width: "80px" }}>
                                                Thao tác
                                            </TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {cartItems.map((item) => {
                                            if (!item || !item.productDTO) {
                                                return null;
                                            }

                                            const product = item.productDTO;
                                            const inventoryStatus = getInventoryStatus(item);
                                            const isSelected = selectedItems.has(product.productID);

                                            return (
                                                <TableRow key={product.productID}>
                                                    <TableCell padding="checkbox">
                                                        <Checkbox
                                                            checked={isSelected}
                                                            onChange={(e) => handleSelectItem(product.productID, e.target.checked)}
                                                            disabled={
                                                                inventoryStatus.status === "out-of-stock" || inventoryStatus.status === "insufficient"
                                                            }
                                                        />
                                                    </TableCell>
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
                                                                    flexShrink: 0,
                                                                }}
                                                            />
                                                            <Box sx={{ flex: 1, minWidth: 0 }}>
                                                                <Typography
                                                                    variant="subtitle1"
                                                                    sx={{ fontWeight: "medium", wordBreak: "break-word" }}
                                                                >
                                                                    {product.title}
                                                                </Typography>
                                                                <Typography variant="body2" color="text.secondary" sx={{ wordBreak: "break-word" }}>
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
                                                        <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 1 }}>
                                                            <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
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
                                                                />
                                                                <IconButton
                                                                    size="small"
                                                                    onClick={() => handleQuantityUpdate(product, item.quantity + 1)}
                                                                    disabled={inventoryStatus.status === "out-of-stock"}
                                                                >
                                                                    <Add />
                                                                </IconButton>
                                                            </Box>
                                                            {/* Di chuyển cảnh báo ra ngoài để không làm chèn TextField */}
                                                            {inventoryStatus.status === "insufficient" && (
                                                                <Typography
                                                                    variant="caption"
                                                                    color="error"
                                                                    sx={{ fontSize: "0.75rem", textAlign: "center" }}
                                                                >
                                                                    Tối đa: {product.quantity}
                                                                </Typography>
                                                            )}
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
                                                            <IconButton color="error" onClick={() => handleRemoveItem(product)} size="small">
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
                                    Order Summary
                                </Typography>

                                {getSelectedItems().length > 0 && (
                                    <Box sx={{ mb: 2, p: 2, backgroundColor: "primary.50", borderRadius: 1 }}>
                                        <Typography variant="body2" color="primary.main" sx={{ fontWeight: "medium" }}>
                                            Đã chọn {getSelectedItemsCount()} sản phẩm ({getSelectedItems().length} loại)
                                        </Typography>
                                    </Box>
                                )}

                                <Box sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}>
                                    <Typography variant="body1">Tạm tính (chưa VAT):</Typography>
                                    <Typography variant="body1">{formatPrice(getSelectedTotalExcludingVAT())}</Typography>
                                </Box>

                                <Box sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}>
                                    <Typography variant="body1">VAT (10%):</Typography>
                                    <Typography variant="body1">{formatPrice(getSelectedVATAmount())}</Typography>
                                </Box>

                                <Divider sx={{ my: 2 }} />

                                <Box sx={{ display: "flex", justifyContent: "space-between", mb: 3 }}>
                                    <Typography variant="h6" sx={{ fontWeight: "bold" }}>
                                        Tổng cộng:
                                    </Typography>
                                    <Typography variant="h6" sx={{ fontWeight: "bold", color: "primary.main" }}>
                                        {formatPrice(getSelectedCartTotal())}
                                    </Typography>
                                </Box>

                                <Button
                                    variant="contained"
                                    fullWidth
                                    size="large"
                                    onClick={handleCheckout}
                                    endIcon={<ArrowForward />}
                                    sx={{ mb: 2 }}
                                    disabled={getSelectedItems().length === 0}
                                >
                                    {getSelectedItems().length === 0 ? "Chọn sản phẩm để thanh toán" : "Tiến hành thanh toán"}
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
