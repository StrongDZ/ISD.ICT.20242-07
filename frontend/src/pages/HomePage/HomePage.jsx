import React, { useState, useEffect } from "react";
import { Box, Container, Typography, Grid, Button, Card, CardContent, CardMedia, Chip } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { productService } from "../../services/productService";
import ProductCard from "../../components/Product/ProductCard";
import LoadingSpinner from "../../components/Common/LoadingSpinner";

const HomePage = () => {
    const navigate = useNavigate();
    const [featuredProducts, setFeaturedProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        loadFeaturedProducts();
    }, []);

    const loadFeaturedProducts = async () => {
        try {
            setLoading(true);
            setError(null);

            // Try to get paginated response first, fallback to non-paginated
            let products = [];
            try {
                const response = await productService.getAllProducts(0, 8); // Get first 8 products
                products = response.content || response;
            } catch (apiError) {
                // Fallback to non-paginated API
                const response = await productService.getAllProductsNoPagination();
                products = Array.isArray(response) ? response.slice(0, 8) : [];
            }

            setFeaturedProducts(products);
        } catch (err) {
            console.error("Error loading featured products:", err);
            setError("Failed to load products");
            // Use mock data as last resort
            const mockProducts = productService.getMockProducts().slice(0, 8);
            setFeaturedProducts(mockProducts);
        } finally {
            setLoading(false);
        }
    };

    const handleCategoryClick = (category) => {
        navigate(`/products?category=${category}`);
    };

    const handleViewAllProducts = () => {
        navigate("/products");
    };

    const categories = [
        {
            name: "Books",
            value: "book",
            description: "Discover amazing books across all genres",
            color: "primary",
            image: "https://via.placeholder.com/300x200/0066cc/ffffff?text=Books",
        },
        {
            name: "CDs",
            value: "cd",
            description: "Listen to your favorite music albums",
            color: "warning",
            image: "https://via.placeholder.com/300x200/ff9800/ffffff?text=CDs",
        },
        {
            name: "DVDs",
            value: "dvd",
            description: "Watch movies and TV shows",
            color: "secondary",
            image: "https://via.placeholder.com/300x200/9c27b0/ffffff?text=DVDs",
        },
    ];

    if (loading) {
        return <LoadingSpinner message="Loading homepage..." />;
    }

    return (
        <Box>
            {/* Hero Section */}
            <Box
                sx={{
                    background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                    color: "white",
                    py: 8,
                    mb: 6,
                }}
            >
                <Container maxWidth="lg">
                    <Grid container spacing={4} alignItems="center">
                        <Grid item xs={12} md={6}>
                            <Typography variant="h2" component="h1" gutterBottom fontWeight="bold">
                                Welcome to AIMS
                            </Typography>
                            <Typography variant="h5" sx={{ mb: 4, opacity: 0.9 }}>
                                Your one-stop destination for books, music, and movies
                            </Typography>
                            <Button
                                variant="contained"
                                size="large"
                                onClick={handleViewAllProducts}
                                sx={{
                                    backgroundColor: "white",
                                    color: "primary.main",
                                    "&:hover": {
                                        backgroundColor: "grey.100",
                                    },
                                }}
                            >
                                Browse All Products
                            </Button>
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <Box
                                component="img"
                                src="https://via.placeholder.com/500x300/667eea/ffffff?text=AIMS+Store"
                                alt="AIMS Store"
                                sx={{
                                    width: "100%",
                                    height: "auto",
                                    borderRadius: 2,
                                    boxShadow: 3,
                                }}
                            />
                        </Grid>
                    </Grid>
                </Container>
            </Box>

            <Container maxWidth="lg">
                {/* Categories Section */}
                <Box sx={{ mb: 8 }}>
                    <Typography variant="h4" component="h2" gutterBottom textAlign="center" fontWeight="bold">
                        Shop by Category
                    </Typography>
                    <Typography variant="body1" textAlign="center" color="text.secondary" sx={{ mb: 4 }}>
                        Explore our wide selection of products
                    </Typography>

                    <Grid container spacing={4}>
                        {categories.map((category) => (
                            <Grid item xs={12} md={4} key={category.value}>
                                <Card
                                    sx={{
                                        cursor: "pointer",
                                        transition: "transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out",
                                        "&:hover": {
                                            transform: "translateY(-4px)",
                                            boxShadow: 4,
                                        },
                                        height: "100%",
                                    }}
                                    onClick={() => handleCategoryClick(category.value)}
                                >
                                    <CardMedia component="img" height="200" image={category.image} alt={category.name} />
                                    <CardContent>
                                        <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
                                            <Typography variant="h5" component="h3" fontWeight="bold">
                                                {category.name}
                                            </Typography>
                                            <Chip label="Explore" color={category.color} size="small" sx={{ ml: 2 }} />
                                        </Box>
                                        <Typography variant="body2" color="text.secondary">
                                            {category.description}
                                        </Typography>
                                    </CardContent>
                                </Card>
                            </Grid>
                        ))}
                    </Grid>
                </Box>

                {/* Featured Products Section */}
                <Box sx={{ mb: 8 }}>
                    <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 4 }}>
                        <Box>
                            <Typography variant="h4" component="h2" gutterBottom fontWeight="bold">
                                Featured Products
                            </Typography>
                            <Typography variant="body1" color="text.secondary">
                                Check out our most popular items
                            </Typography>
                        </Box>
                        <Button variant="outlined" onClick={handleViewAllProducts}>
                            View All
                        </Button>
                    </Box>

                    {error && (
                        <Typography color="error" sx={{ mb: 2 }}>
                            {error}
                        </Typography>
                    )}

                    <Grid container spacing={3}>
                        {featuredProducts.map((product) => (
                            <Grid item xs={12} sm={6} md={4} lg={3} key={product.productID}>
                                <ProductCard product={product} />
                            </Grid>
                        ))}
                    </Grid>
                </Box>

                {/* Statistics Section */}
                <Box
                    sx={{
                        backgroundColor: "grey.50",
                        borderRadius: 2,
                        p: 6,
                        mb: 8,
                        textAlign: "center",
                    }}
                >
                    <Typography variant="h4" component="h2" gutterBottom fontWeight="bold">
                        Why Choose AIMS?
                    </Typography>
                    <Grid container spacing={4} sx={{ mt: 2 }}>
                        <Grid item xs={12} md={4}>
                            <Typography variant="h3" color="primary" fontWeight="bold">
                                10,000+
                            </Typography>
                            <Typography variant="h6" color="text.secondary">
                                Products Available
                            </Typography>
                        </Grid>
                        <Grid item xs={12} md={4}>
                            <Typography variant="h3" color="primary" fontWeight="bold">
                                24/7
                            </Typography>
                            <Typography variant="h6" color="text.secondary">
                                Customer Support
                            </Typography>
                        </Grid>
                        <Grid item xs={12} md={4}>
                            <Typography variant="h3" color="primary" fontWeight="bold">
                                Fast
                            </Typography>
                            <Typography variant="h6" color="text.secondary">
                                Delivery Service
                            </Typography>
                        </Grid>
                    </Grid>
                </Box>
            </Container>
        </Box>
    );
};

export default HomePage;
