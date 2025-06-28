import React, { useState } from "react";
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
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Alert,
  CircularProgress,
} from "@mui/material";
import {
  Add,
  Remove,
  Delete,
  ShoppingCart,
  ArrowForward,
} from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { useCart } from "../../contexts/CartContext";
import { orderService } from "../../services/orderService";

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
  } = useCart();
  const [isLoading, setIsLoading] = useState(false);
  const [inventoryErrorDialog, setInventoryErrorDialog] = useState({
    open: false,
    insufficientItems: [],
  });

  const formatPrice = (price) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  };

  const handleQuantityChange = (product, newQuantity) => {
    updateCartItem(product, newQuantity);
  };

  const handleRemoveItem = (product) => {
    removeFromCart(product);
  };

  const handleClearCart = () => {
    if (window.confirm("Are you sure you want to clear your cart?")) {
      clearCart();
    }
  };

  const handleContinueShopping = () => {
    navigate("/products");
  };

  const handleCheckout = async () => {
    setIsLoading(true);
    try {
      // Kiểm tra tồn kho trước khi chuyển đến checkout
      const cartItemsForCheck = cartItems.map((item) => ({
        productDTO: item.product,
        quantity: item.quantity,
      }));

      const inventoryCheck = await orderService.checkInventory(
        cartItemsForCheck
      );
      console.log("Inventory check result:", inventoryCheck);

      if (!inventoryCheck.success) {
        // Hiển thị dialog với danh sách sản phẩm không đủ tồn kho
        setInventoryErrorDialog({
          open: true,
          insufficientItems: inventoryCheck.insufficientItems,
        });
        return;
      }

      // Nếu đủ tồn kho, chuyển đến trang checkout
      navigate("/checkout");
    } catch (error) {
      alert(error.message || "Không thể kiểm tra tồn kho. Vui lòng thử lại.");
    } finally {
      setIsLoading(false);
    }
  };

  if (cartItems.length === 0) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Paper sx={{ p: 4, textAlign: "center" }}>
          <ShoppingCart sx={{ fontSize: 80, color: "grey.400", mb: 2 }} />
          <Typography variant="h4" gutterBottom>
            Your Cart is Empty
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
            Looks like you haven't added any items to your cart yet.
          </Typography>
          <Button
            variant="contained"
            size="large"
            onClick={handleContinueShopping}
            startIcon={<ShoppingCart />}
          >
            Start Shopping
          </Button>
        </Paper>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography
        variant="h3"
        component="h1"
        gutterBottom
        sx={{ fontWeight: "bold" }}
      >
        Shopping Cart
      </Typography>

      <Grid container spacing={3}>
        {/* Cart Items */}
        <Grid item xs={12} md={8}>
          <Paper sx={{ mb: 3 }}>
            <Box
              sx={{
                p: 2,
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <Typography variant="h6">
                Cart Items ({getCartCount()}{" "}
                {getCartCount() === 1 ? "item" : "items"})
              </Typography>
              <Button
                variant="outlined"
                color="error"
                startIcon={<Delete />}
                onClick={handleClearCart}
                size="small"
              >
                Clear Cart
              </Button>
            </Box>

            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Product</TableCell>
                    <TableCell align="center">Quantity</TableCell>
                    <TableCell align="right">Price</TableCell>
                    <TableCell align="right">Total</TableCell>
                    <TableCell align="center">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {cartItems.map((item) => (
                    <TableRow key={item.product.productID}>
                      <TableCell>
                        <Box
                          sx={{ display: "flex", alignItems: "center", gap: 2 }}
                        >
                          <img
                            src={item.product.imageURL}
                            alt={item.product.title}
                            style={{
                              width: 60,
                              height: 60,
                              objectFit: "cover",
                              borderRadius: 4,
                            }}
                          />
                          <Box>
                            <Typography
                              variant="subtitle1"
                              sx={{ fontWeight: "medium" }}
                            >
                              {item.product.title}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                              ID: {item.product.productID}
                            </Typography>
                          </Box>
                        </Box>
                      </TableCell>

                      <TableCell align="center">
                        <Box
                          sx={{
                            display: "flex",
                            alignItems: "center",
                            gap: 1,
                            justifyContent: "center",
                          }}
                        >
                          <IconButton
                            size="small"
                            onClick={() =>
                              handleQuantityChange(
                                item.product,
                                item.quantity - 1
                              )
                            }
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
                                handleQuantityChange(item.product, value);
                              }
                            }}
                            inputProps={{
                              style: { textAlign: "center", width: "50px" },
                              min: 1,
                            }}
                          />
                          <IconButton
                            size="small"
                            onClick={() =>
                              handleQuantityChange(
                                item.product,
                                item.quantity + 1
                              )
                            }
                          >
                            <Add />
                          </IconButton>
                        </Box>
                      </TableCell>

                      <TableCell align="right">
                        {formatPrice(item.product.price)}
                      </TableCell>

                      <TableCell align="right">
                        <Typography
                          variant="subtitle1"
                          sx={{ fontWeight: "bold" }}
                        >
                          {formatPrice(item.product.price * item.quantity)}
                        </Typography>
                      </TableCell>

                      <TableCell align="center">
                        <IconButton
                          color="error"
                          onClick={() => handleRemoveItem(item.product)}
                        >
                          <Delete />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
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

              <Box
                sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}
              >
                <Typography variant="body1">Subtotal (excl. VAT):</Typography>
                <Typography variant="body1">
                  {formatPrice(getTotalExcludingVAT())}
                </Typography>
              </Box>

              <Box
                sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}
              >
                <Typography variant="body1">VAT (10%):</Typography>
                <Typography variant="body1">
                  {formatPrice(getVATAmount())}
                </Typography>
              </Box>

              <Divider sx={{ my: 2 }} />

              <Box
                sx={{ display: "flex", justifyContent: "space-between", mb: 3 }}
              >
                <Typography variant="h6" sx={{ fontWeight: "bold" }}>
                  Total:
                </Typography>
                <Typography
                  variant="h6"
                  sx={{ fontWeight: "bold", color: "primary.main" }}
                >
                  {formatPrice(getCartTotal())}
                </Typography>
              </Box>

              <Button
                variant="contained"
                fullWidth
                size="large"
                onClick={handleCheckout}
                disabled={isLoading}
                endIcon={
                  isLoading ? <CircularProgress size={20} /> : <ArrowForward />
                }
                sx={{ mb: 2 }}
              >
                {isLoading ? "Đang kiểm tra..." : "Proceed to Checkout"}
              </Button>

              <Button
                variant="outlined"
                fullWidth
                onClick={handleContinueShopping}
              >
                Continue Shopping
              </Button>

              <Box
                sx={{
                  mt: 3,
                  p: 2,
                  backgroundColor: "grey.50",
                  borderRadius: 1,
                }}
              >
                <Typography
                  variant="body2"
                  color="text.secondary"
                  sx={{ textAlign: "center" }}
                >
                  <strong>Free shipping</strong> for orders over 100,000 VND
                  <br />
                  (up to 25,000 VND discount)
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Inventory Error Dialog */}
      <Dialog
        open={inventoryErrorDialog.open}
        onClose={() =>
          setInventoryErrorDialog({ open: false, insufficientItems: [] })
        }
        maxWidth="md"
        fullWidth
      >
        <DialogTitle sx={{ color: "error.main" }}>Tồn kho không đủ</DialogTitle>
        <DialogContent>
          <Typography gutterBottom>
            Một số sản phẩm trong giỏ hàng không đủ tồn kho. Vui lòng cập nhật
            số lượng hoặc xóa sản phẩm:
          </Typography>
          <Box sx={{ mt: 2 }}>
            {(inventoryErrorDialog.insufficientItems || []).map(
              (item, index) => (
                <Box
                  key={index}
                  sx={{
                    p: 2,
                    mb: 1,
                    border: 1,
                    borderColor: "error.light",
                    borderRadius: 1,
                    bgcolor: "error.50",
                  }}
                >
                  <Typography variant="subtitle2" sx={{ fontWeight: "bold" }}>
                    {item.productDTO.title}
                  </Typography>
                  <Typography variant="body2" color="error.main">
                    Yêu cầu: {item.quantity} | Có sẵn:{" "}
                    {item.productDTO.quantity} | Thiếu:{" "}
                    {item.quantity - item.productDTO.quantity}
                  </Typography>
                </Box>
              )
            )}
          </Box>
          <Alert severity="warning" sx={{ mt: 2 }}>
            <Typography variant="body2">
              Vui lòng cập nhật số lượng sản phẩm hoặc xóa các sản phẩm không đủ
              tồn kho trước khi tiếp tục.
            </Typography>
          </Alert>
        </DialogContent>
        <DialogActions>
          <Button
            onClick={() =>
              setInventoryErrorDialog({ open: false, insufficientItems: [] })
            }
            variant="contained"
          >
            Đóng
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default CartPage;
