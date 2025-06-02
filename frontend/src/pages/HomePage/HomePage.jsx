import React, { useState, useEffect } from "react";
import { Container, Typography, Box, Grid, Card, CardContent, CardMedia, Button, Chip } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { productService } from "../../services/productService";
import ProductCard from "../../components/Product/ProductCard";
import LoadingSpinner from "../../components/Common/LoadingSpinner";

const HomePage = () => {
    const navigate = useNavigate();
    const [featuredProducts, setFeaturedProducts] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadFeaturedProducts = () => {
            try {
                // For now, using mock data. Replace with actual API call later
                const mockProducts = productService.getMockProducts();
                const shuffled = mockProducts.sort(() => 0.5 - Math.random());
                setFeaturedProducts(shuffled.slice(0, 8)); // Show 8 featured products
            } catch (error) {
                console.error("Error loading featured products:", error);
            } finally {
                setLoading(false);
            }
        };

        loadFeaturedProducts();
    }, []);

    const categories = [
        {
            name: "Books",
            value: "book",
            description: "Discover knowledge and stories across all genres",
            color: "#2196F3",
            image: "https://via.placeholder.com/300x200/2196F3/ffffff?text=Books",
            features: ["Programming", "Literature", "History", "Non-Fiction"],
        },
        {
            name: "CDs",
            value: "cd",
            description: "Listen to your favorite music in digital quality",
            color: "#FF9800",
            image: "https://via.placeholder.com/300x200/FF9800/ffffff?text=CDs",
            features: ["Rock", "Pop", "Electronic", "Classical"],
        },
        {
            name: "LPs (Vinyl)",
            value: "lp",
            description: "Experience the warmth of analog sound",
            color: "#4CAF50",
            image: "https://via.placeholder.com/300x200/4CAF50/ffffff?text=Vinyl+LPs",
            features: ["180g Pressing", "Audiophile Quality", "Limited Editions", "Collector's Items"],
        },
        {
            name: "DVDs",
            value: "dvd",
            description: "Watch amazing movies and shows in high definition",
            color: "#9C27B0",
            image: "https://via.placeholder.com/300x200/9C27B0/ffffff?text=DVDs",
            features: ["4K Ultra HD", "Blu-ray", "Special Editions", "Documentaries"],
        },
    ];

    const handleCategoryClick = (category) => {
        navigate(`/products?category=${category}`);
    };

    const handleViewAllProducts = () => {
        navigate("/products");
    };

    if (loading) {
        return <LoadingSpinner />;
    }

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            {/* Hero Section */}
            <Box
                sx={{
                    textAlign: "center",
                    py: 8,
                    background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                    borderRadius: 2,
                    color: "white",
                    mb: 6,
                }}
            >
                <Typography variant="h2" component="h1" gutterBottom sx={{ fontWeight: "bold" }}>
                    Welcome to AIMS
                </Typography>
                <Typography variant="h5" sx={{ mb: 4, opacity: 0.9 }}>
                    Advanced Interactive Media Store
                </Typography>
                <Typography variant="body1" sx={{ mb: 4, maxWidth: 600, mx: "auto" }}>
                    Discover a vast collection of books, CDs, vinyl records, and DVDs. Find your next favorite read, listen to amazing music on
                    premium formats, or watch incredible movies in stunning quality.
                </Typography>
                <Button
                    variant="contained"
                    size="large"
                    onClick={handleViewAllProducts}
                    sx={{
                        backgroundColor: "white",
                        color: "primary.main",
                        px: 4,
                        py: 1.5,
                        fontSize: "1.1rem",
                        "&:hover": {
                            backgroundColor: "grey.100",
                            transform: "translateY(-2px)",
                            boxShadow: 4,
                        },
                        transition: "all 0.3s ease-in-out",
                    }}
                >
                    Browse All Products
                </Button>
            </Box>

            {/* Categories Section */}
            <Box sx={{ mb: 6 }}>
                <Typography variant="h4" component="h2" gutterBottom sx={{ textAlign: "center", mb: 4 }}>
                    Shop by Category
                </Typography>
                <Grid container spacing={3}>
                    {categories.map((category) => (
                        <Grid item xs={12} sm={6} md={3} key={category.value}>
                            <Card
                                sx={{
                                    height: "100%",
                                    cursor: "pointer",
                                    transition: "all 0.3s ease-in-out",
                                    "&:hover": {
                                        transform: "translateY(-8px)",
                                        boxShadow: 6,
                                    },
                                }}
                                onClick={() => handleCategoryClick(category.value)}
                            >
                                <CardMedia component="img" height="180" image={category.image} alt={category.name} />
                                <CardContent sx={{ textAlign: "center", p: 2 }}>
                                    <Typography variant="h6" component="h3" gutterBottom sx={{ fontWeight: "bold" }}>
                                        {category.name}
                                    </Typography>
                                    <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                                        {category.description}
                                    </Typography>
                                    <Box sx={{ display: "flex", flexWrap: "wrap", gap: 0.5, justifyContent: "center" }}>
                                        {category.features.slice(0, 2).map((feature, index) => (
                                            <Chip
                                                key={index}
                                                label={feature}
                                                size="small"
                                                sx={{
                                                    fontSize: "0.7rem",
                                                    backgroundColor: category.color + "20",
                                                    color: category.color,
                                                    border: `1px solid ${category.color}40`,
                                                }}
                                            />
                                        ))}
                                    </Box>
                                </CardContent>
                            </Card>
                        </Grid>
                    ))}
                </Grid>
            </Box>

            {/* Featured Products Section */}
            <Box>
                <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 4 }}>
                    <Typography variant="h4" component="h2">
                        Featured Products
                    </Typography>
                    <Button
                        variant="outlined"
                        onClick={handleViewAllProducts}
                        sx={{
                            "&:hover": {
                                transform: "translateY(-2px)",
                                boxShadow: 2,
                            },
                            transition: "all 0.3s ease-in-out",
                        }}
                    >
                        View All Products
                    </Button>
                </Box>
                <Grid container spacing={3}>
                    {featuredProducts.map((product) => (
                        <Grid item xs={12} sm={6} md={4} lg={3} key={product.productID}>
                            <ProductCard product={product} />
                        </Grid>
                    ))}
                </Grid>
            </Box>

            {/* Features Section */}
            <Box sx={{ mt: 8, py: 6, backgroundColor: "grey.50", borderRadius: 2 }}>
                <Typography variant="h4" component="h2" gutterBottom sx={{ textAlign: "center", mb: 4 }}>
                    Why Choose AIMS?
                </Typography>
                <Grid container spacing={4}>
                    <Grid item xs={12} md={3}>
                        <Box sx={{ textAlign: "center", p: 2 }}>
                            <Typography variant="h6" gutterBottom sx={{ fontSize: "3rem", mb: 1 }}>
                                🚀
                            </Typography>
                            <Typography variant="h6" gutterBottom>
                                Fast Delivery
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Get your orders delivered quickly with our efficient delivery system. Rush delivery available within 2 hours for Hanoi
                                inner city.
                            </Typography>
                        </Box>
                    </Grid>
                    <Grid item xs={12} md={3}>
                        <Box sx={{ textAlign: "center", p: 2 }}>
                            <Typography variant="h6" gutterBottom sx={{ fontSize: "3rem", mb: 1 }}>
                                📚
                            </Typography>
                            <Typography variant="h6" gutterBottom>
                                Wide Selection
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Choose from thousands of books, CDs, vinyl records, and DVDs across all genres and categories with premium quality.
                            </Typography>
                        </Box>
                    </Grid>
                    <Grid item xs={12} md={3}>
                        <Box sx={{ textAlign: "center", p: 2 }}>
                            <Typography variant="h6" gutterBottom sx={{ fontSize: "3rem", mb: 1 }}>
                                💳
                            </Typography>
                            <Typography variant="h6" gutterBottom>
                                Secure Payment
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Shop with confidence using our secure payment system powered by VNPay. Multiple payment options available.
                            </Typography>
                        </Box>
                    </Grid>
                    <Grid item xs={12} md={3}>
                        <Box sx={{ textAlign: "center", p: 2 }}>
                            <Typography variant="h6" gutterBottom sx={{ fontSize: "3rem", mb: 1 }}>
                                🎁
                            </Typography>
                            <Typography variant="h6" gutterBottom>
                                Free Shipping
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Enjoy free shipping on orders over 100,000 VND (up to 25,000 VND discount). Save more on bulk purchases.
                            </Typography>
                        </Box>
                    </Grid>
                </Grid>
            </Box>

            {/* Statistics Section */}
            <Box sx={{ mt: 6, py: 4, textAlign: "center" }}>
                <Typography variant="h5" component="h2" gutterBottom sx={{ mb: 4 }}>
                    Trusted by Thousands
                </Typography>
                <Grid container spacing={4}>
                    <Grid item xs={12} sm={3}>
                        <Typography variant="h3" color="primary" sx={{ fontWeight: "bold" }}>
                            1000+
                        </Typography>
                        <Typography variant="body1" color="text.secondary">
                            Concurrent Users
                        </Typography>
                    </Grid>
                    <Grid item xs={12} sm={3}>
                        <Typography variant="h3" color="primary" sx={{ fontWeight: "bold" }}>
                            20+
                        </Typography>
                        <Typography variant="body1" color="text.secondary">
                            Product Categories
                        </Typography>
                    </Grid>
                    <Grid item xs={12} sm={3}>
                        <Typography variant="h3" color="primary" sx={{ fontWeight: "bold" }}>
                            24/7
                        </Typography>
                        <Typography variant="body1" color="text.secondary">
                            Service Available
                        </Typography>
                    </Grid>
                    <Grid item xs={12} sm={3}>
                        <Typography variant="h3" color="primary" sx={{ fontWeight: "bold" }}>
                            2hrs
                        </Typography>
                        <Typography variant="body1" color="text.secondary">
                            Rush Delivery
                        </Typography>
                    </Grid>
                </Grid>
            </Box>
        </Container>
    );
};

export default HomePage;
