import React, { useState, useEffect } from "react";
import {
    Container,
    Grid,
    Typography,
    Box,
    Button,
    Card,
    CardContent,
    Chip,
    Divider,
    Paper,
    TextField,
    IconButton,
    Alert,
    Snackbar,
    Breadcrumbs,
    Link,
    CircularProgress,
} from "@mui/material";
import { Add, Remove, ShoppingCart, ArrowBack, Favorite, Share } from "@mui/icons-material";
import { useParams, useNavigate } from "react-router-dom";
import { productService } from "../../services/productService";
import { useCart } from "../../contexts/CartContext";
import { useAuth } from "../../contexts/AuthContext";
import ProductCard from "../../components/Product/ProductCard";
import LoadingSpinner from "../../components/Common/LoadingSpinner";
import { getCategoryColor } from "../../utils/getCategoryColor";

const ProductDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { addToCart } = useCart();
    const { isAuthenticated } = useAuth();

    const [product, setProduct] = useState(null);
    const [relatedProducts, setRelatedProducts] = useState([]);
    const [quantity, setQuantity] = useState(1);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [addingToCart, setAddingToCart] = useState(false);
    const [cartMessage, setCartMessage] = useState("");
    const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });

    useEffect(() => {
        if (id) {
            loadProductDetails();
        }
    }, [id]);

    const loadProductDetails = async () => {
        try {
            setLoading(true);
            setError(null);

            const productData = await productService.getProductById(id);
            setProduct(productData);

            // Load related products (same category)
            const allProducts = productService.getMockProducts();
            const related = allProducts.filter((p) => p.category === productData.category && p.productID !== id).slice(0, 4);
            setRelatedProducts(related);
        } catch (err) {
            console.error("Error loading product details:", err);
            setError(err.message || "Failed to load product details");

            // Try to find product in mock data as fallback
            try {
                const mockProducts = productService.getMockProducts();
                const mockProduct = mockProducts.find((p) => p.productID === id);
                if (mockProduct) {
                    setProduct(mockProduct);
                    setError(null);
                } else {
                    setError("Product not found");
                }
            } catch (mockError) {
                setError("Product not found");
            }
        } finally {
            setLoading(false);
        }
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(price);
    };

    const handleQuantityChange = (change) => {
        const newQuantity = Math.max(1, Math.min(quantity + change, product?.quantity || 0));
        if (newQuantity >= 1 && newQuantity <= (product?.quantity || 0)) {
            setQuantity(newQuantity);
        }
    };

    const handleAddToCart = async () => {
        try {
            setAddingToCart(true);
            setCartMessage("");

            await addToCart(product, quantity);
            setCartMessage("Product added to cart successfully!");

            // Reset quantity after successful add
            setQuantity(1);
        } catch (err) {
            console.error("Error adding to cart:", err);
            setCartMessage("Failed to add product to cart: " + err.response.data.message);
            loadProductDetails();
        } finally {
            setAddingToCart(false);
        }
    };



    const renderCategorySpecificInfo = () => {
        if (!product) return null;

        switch (product.category) {
            case "book":
                return (
                    <Card sx={{ mb: 3 }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Book Details
                            </Typography>
                            <Grid container spacing={2}>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Author(s)
                                    </Typography>
                                    <Typography variant="body1">{product.authors}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Publisher
                                    </Typography>
                                    <Typography variant="body1">{product.publisher}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Pages
                                    </Typography>
                                    <Typography variant="body1">{product.numberOfPages}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Language
                                    </Typography>
                                    <Typography variant="body1">{product.language}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Genre
                                    </Typography>
                                    <Typography variant="body1">{product.genre}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Cover Type
                                    </Typography>
                                    <Typography variant="body1">{product.coverType}</Typography>
                                </Grid>
                                <Grid item xs={12}>
                                    <Typography variant="body2" color="text.secondary">
                                        ISBN
                                    </Typography>
                                    <Typography variant="body1">{product.isbn}</Typography>
                                </Grid>
                            </Grid>
                        </CardContent>
                    </Card>
                );

            case "cd":
                return (
                    <Card sx={{ mb: 3 }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Album Details
                            </Typography>
                            <Grid container spacing={2}>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Artist
                                    </Typography>
                                    <Typography variant="body1">{product.artist}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Record Label
                                    </Typography>
                                    <Typography variant="body1">{product.recordLabel}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Music Type
                                    </Typography>
                                    <Typography variant="body1">{product.musicType}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Release Date
                                    </Typography>
                                    <Typography variant="body1">
                                        {product.releaseDate ? new Date(product.releaseDate).toLocaleDateString() : "N/A"}
                                    </Typography>
                                </Grid>
                                <Grid item xs={12}>
                                    <Typography variant="body2" color="text.secondary">
                                        Tracklist
                                    </Typography>
                                    <Typography variant="body1">{product.tracklist}</Typography>
                                </Grid>
                            </Grid>
                        </CardContent>
                    </Card>
                );

            case "dvd":
                return (
                    <Card sx={{ mb: 3 }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Movie Details
                            </Typography>
                            <Grid container spacing={2}>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Director
                                    </Typography>
                                    <Typography variant="body1">{product.director}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Studio
                                    </Typography>
                                    <Typography variant="body1">{product.studio}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Runtime
                                    </Typography>
                                    <Typography variant="body1">{product.runtime}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Disc Type
                                    </Typography>
                                    <Typography variant="body1">{product.discType}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Language
                                    </Typography>
                                    <Typography variant="body1">{product.language}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Genre
                                    </Typography>
                                    <Typography variant="body1">{product.genre}</Typography>
                                </Grid>
                                <Grid item xs={12}>
                                    <Typography variant="body2" color="text.secondary">
                                        Subtitles
                                    </Typography>
                                    <Typography variant="body1">{product.subtitle}</Typography>
                                </Grid>
                            </Grid>
                        </CardContent>
                    </Card>
                );

            case "lp":
                return (
                    <Card sx={{ mb: 3 }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Vinyl Details
                            </Typography>
                            <Grid container spacing={2}>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Artist
                                    </Typography>
                                    <Typography variant="body1">{product.artist}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Record Label
                                    </Typography>
                                    <Typography variant="body1">{product.recordLabel}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Vinyl Size
                                    </Typography>
                                    <Typography variant="body1">{product.vinylSize}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        RPM
                                    </Typography>
                                    <Typography variant="body1">{product.rpm}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Music Type
                                    </Typography>
                                    <Typography variant="body1">{product.musicType}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Release Date
                                    </Typography>
                                    <Typography variant="body1">
                                        {product.releaseDate ? new Date(product.releaseDate).toLocaleDateString() : "N/A"}
                                    </Typography>
                                </Grid>
                                <Grid item xs={12}>
                                    <Typography variant="body2" color="text.secondary">
                                        Tracklist
                                    </Typography>
                                    <Typography variant="body1">{product.tracklist}</Typography>
                                </Grid>
                            </Grid>
                        </CardContent>
                    </Card>
                );

            default:
                return null;
        }
    };

    if (loading) {
        return <LoadingSpinner message="Loading product details..." />;
    }

    if (error && !product) {
        return (
            <Container maxWidth="lg" sx={{ py: 4 }}>
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
                <Button startIcon={<ArrowBack />} onClick={() => navigate("/products")}>
                    Back to Products
                </Button>
            </Container>
        );
    }

    if (!product) {
        return (
            <Container maxWidth="lg" sx={{ py: 4 }}>
                <Alert severity="info">Product not found</Alert>
                <Button startIcon={<ArrowBack />} onClick={() => navigate("/products")} sx={{ mt: 2 }}>
                    Back to Products
                </Button>
            </Container>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            {/* Breadcrumbs */}
            <Breadcrumbs sx={{ mb: 3 }}>
                <Link
                    color="inherit"
                    href="#"
                    onClick={(e) => {
                        e.preventDefault();
                        navigate("/");
                    }}
                >
                    Home
                </Link>
                <Link
                    color="inherit"
                    href="#"
                    onClick={(e) => {
                        e.preventDefault();
                        navigate("/products");
                    }}
                >
                    Products
                </Link>
                <Link
                    color="inherit"
                    href="#"
                    onClick={(e) => {
                        e.preventDefault();
                        navigate(`/products?category=${product.category}`);
                    }}
                >
                    {product.category?.toUpperCase()}
                </Link>
                <Typography color="text.primary">{product.title}</Typography>
            </Breadcrumbs>

            {/* Back Button */}
            <Button startIcon={<ArrowBack />} onClick={() => navigate(-1)} sx={{ mb: 3 }}>
                Back
            </Button>

            {/* Product Main Info */}
            <Grid container spacing={4} sx={{ mb: 4 }}>
                {/* Product Image */}
                <Grid item xs={12} md={5}>
                    <Paper elevation={2} sx={{ p: 2 }}>
                        <img
                            src={product.imageURL}
                            alt={product.title}
                            style={{
                                width: "100%",
                                maxHeight: "100%",
                                objectFit: "cover",
                                borderRadius: "8px",
                            }}
                        />
                    </Paper>
                </Grid>

                {/* Product Details */}
                <Grid item xs={12} md={7}>
                    <Box sx={{ mb: 2 }}>
                        <Chip label={product.category?.toUpperCase()} color={getCategoryColor(product.category)} sx={{ mb: 2 }} />
                        <Typography variant="h4" component="h1" gutterBottom>
                            {product.title}
                        </Typography>
                        <Typography variant="h5" color="primary" sx={{ fontWeight: "bold", mb: 2 }}>
                            {formatPrice(product.price)}
                        </Typography>
                    </Box>

                    <Divider sx={{ my: 2 }} />

                    {/* Stock Status */}
                    <Box sx={{ mb: 3 }}>
                        <Typography variant="body1" sx={{ mb: 1 }}>
                            <strong>Availability:</strong>{" "}
                            {product.quantity > 0 ? (
                                <span style={{ color: "green" }}>In Stock ({product.quantity} available)</span>
                            ) : (
                                <span style={{ color: "red" }}>Out of Stock</span>
                            )}
                        </Typography>
                        {product.eligible && <Chip label="Rush Delivery Available" color="success" size="small" />}
                    </Box>

                    {/* Description */}
                    <Typography variant="body1" sx={{ mb: 3 }}>
                        {product.description}
                    </Typography>

                    {/* Add to Cart Section */}
                    {product.quantity > 0 && (
                        <Paper elevation={1} sx={{ p: 3, mb: 3 }}>
                            <Typography variant="h6" gutterBottom>
                                Add to Cart
                            </Typography>
                            <Box sx={{ display: "flex", alignItems: "center", gap: 2, mb: 2 }}>
                                <Typography variant="body1">Quantity:</Typography>
                                <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                                    <IconButton onClick={() => handleQuantityChange(-1)} disabled={quantity <= 1}>
                                        <Remove />
                                    </IconButton>
                                    <TextField
                                        type="number"
                                        value={quantity}
                                        onChange={(e) => {
                                            const value = parseInt(e.target.value);
                                            if (value >= 1 && value <= product.quantity) {
                                                setQuantity(value);
                                            }
                                        }}
                                        inputProps={{ min: 1, max: product.quantity }}
                                        sx={{ width: "80px" }}
                                        size="small"
                                    />
                                    <IconButton onClick={() => handleQuantityChange(1)} disabled={quantity >= product.quantity}>
                                        <Add />
                                    </IconButton>
                                </Box>
                            </Box>
                            <Button
                                variant="contained"
                                startIcon={addingToCart ? <CircularProgress size={20} /> : <ShoppingCart />}
                                onClick={handleAddToCart}
                                disabled={product.quantity === 0 || addingToCart}
                                size="large"
                                sx={{ mb: 2 }}
                            >
                                {addingToCart ? "Adding..." : "Add to Cart"}
                            </Button>
                            <Box sx={{ display: "flex", gap: 1 }}>
                                <Button variant="outlined" startIcon={<Favorite />} fullWidth>
                                    Add to Wishlist
                                </Button>
                                <Button variant="outlined" startIcon={<Share />} fullWidth>
                                    Share
                                </Button>
                            </Box>
                            {cartMessage && (
                                <Alert
                                    severity={cartMessage.includes("successfully") ? "success" : "error"}
                                    sx={{ mt: 1 }}
                                    onClose={() => setCartMessage("")}
                                >
                                    {cartMessage}
                                </Alert>
                            )}
                        </Paper>
                    )}

                    {/* Product Specifications */}
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Product Information
                            </Typography>
                            <Grid container spacing={2}>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Product ID
                                    </Typography>
                                    <Typography variant="body1">{product.productID}</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant="body2" color="text.secondary">
                                        Weight
                                    </Typography>
                                    <Typography variant="body1">{product.weight} kg</Typography>
                                </Grid>
                                <Grid item xs={12}>
                                    <Typography variant="body2" color="text.secondary">
                                        Dimensions
                                    </Typography>
                                    <Typography variant="body1">{product.dimensions}</Typography>
                                </Grid>
                                <Grid item xs={12}>
                                    <Typography variant="body2" color="text.secondary">
                                        Barcode
                                    </Typography>
                                    <Typography variant="body1">{product.barcode}</Typography>
                                </Grid>
                            </Grid>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            {/* Category Specific Information */}
            {renderCategorySpecificInfo()}

            {/* Related Products */}
            {relatedProducts.length > 0 && (
                <Box sx={{ mt: 6 }}>
                    <Typography variant="h5" gutterBottom>
                        Related Products
                    </Typography>
                    <Grid container spacing={3}>
                        {relatedProducts.map((relatedProduct) => (
                            <Grid item xs={12} sm={6} md={3} key={relatedProduct.productID}>
                                <ProductCard product={relatedProduct} />
                            </Grid>
                        ))}
                    </Grid>
                </Box>
            )}

            {/* Snackbar */}
            <Snackbar
                open={snackbar.open}
                autoHideDuration={3000}
                onClose={() => setSnackbar({ ...snackbar, open: false })}
                anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
            >
                <Alert onClose={() => setSnackbar({ ...snackbar, open: false })} severity={snackbar.severity}>
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Container>
    );
};

export default ProductDetailPage;
