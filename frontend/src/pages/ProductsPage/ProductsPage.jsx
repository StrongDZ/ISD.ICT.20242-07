import React, { useState, useEffect } from "react";
import {
    Container,
    Typography,
    Box,
    Grid,
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Button,
    Pagination,
    Chip,
    Paper,
    Card,
    CardContent,
    InputAdornment,
    Slider,
} from "@mui/material";
import { Search, FilterList, Clear } from "@mui/icons-material";
import { useLocation, useNavigate } from "react-router-dom";
import { productService } from "../../services/productService";
import ProductCard from "../../components/Product/ProductCard";
import LoadingSpinner from "../../components/Common/LoadingSpinner";

const ProductsPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [products, setProducts] = useState([]);
    const [filteredProducts, setFilteredProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage] = useState(20);
    const [filters, setFilters] = useState({
        search: "",
        category: "",
        sortBy: "",
        minPrice: 0,
        maxPrice: 2000000,
    });

    useEffect(() => {
        loadProducts();
    }, []);

    useEffect(() => {
        // Parse URL parameters on mount
        const urlParams = new URLSearchParams(location.search);
        const newFilters = {
            search: urlParams.get("search") || "",
            category: urlParams.get("category") || "",
            sortBy: urlParams.get("sortBy") || "",
            minPrice: parseInt(urlParams.get("minPrice")) || 0,
            maxPrice: parseInt(urlParams.get("maxPrice")) || 2000000,
        };
        setFilters(newFilters);
        setCurrentPage(parseInt(urlParams.get("page")) || 1);
    }, [location.search]);

    useEffect(() => {
        filterAndSortProducts();
    }, [products, filters]);

    const loadProducts = async () => {
        try {
            setLoading(true);
            const mockProducts = productService.getMockProducts();
            setProducts(mockProducts);
        } catch (error) {
            console.error("Error loading products:", error);
        } finally {
            setLoading(false);
        }
    };

    const filterAndSortProducts = () => {
        let filtered = [...products];

        // Apply search filter
        if (filters.search) {
            const searchTerm = filters.search.toLowerCase();
            filtered = filtered.filter(
                (product) =>
                    product.title.toLowerCase().includes(searchTerm) ||
                    product.description.toLowerCase().includes(searchTerm) ||
                    (product.authors && product.authors.toLowerCase().includes(searchTerm)) ||
                    (product.artist && product.artist.toLowerCase().includes(searchTerm)) ||
                    (product.director && product.director.toLowerCase().includes(searchTerm))
            );
        }

        // Apply category filter
        if (filters.category) {
            filtered = filtered.filter((product) => product.category === filters.category);
        }

        // Apply price range filter
        filtered = filtered.filter((product) => product.price >= filters.minPrice && product.price <= filters.maxPrice);

        // Apply sorting
        if (filters.sortBy) {
            switch (filters.sortBy) {
                case "title_asc":
                    filtered.sort((a, b) => a.title.localeCompare(b.title));
                    break;
                case "title_desc":
                    filtered.sort((a, b) => b.title.localeCompare(a.title));
                    break;
                case "price_asc":
                    filtered.sort((a, b) => a.price - b.price);
                    break;
                case "price_desc":
                    filtered.sort((a, b) => b.price - a.price);
                    break;
                case "newest":
                    filtered.sort((a, b) => new Date(b.releaseDate || b.pubDate) - new Date(a.releaseDate || a.pubDate));
                    break;
                default:
                    break;
            }
        }

        setFilteredProducts(filtered);
        setCurrentPage(1); // Reset to first page when filters change
    };

    const updateURL = (newFilters, newPage = currentPage) => {
        const params = new URLSearchParams();
        if (newFilters.search) params.set("search", newFilters.search);
        if (newFilters.category) params.set("category", newFilters.category);
        if (newFilters.sortBy) params.set("sortBy", newFilters.sortBy);
        if (newFilters.minPrice !== 0) params.set("minPrice", newFilters.minPrice);
        if (newFilters.maxPrice !== 2000000) params.set("maxPrice", newFilters.maxPrice);
        if (newPage !== 1) params.set("page", newPage);

        const newUrl = `${location.pathname}${params.toString() ? `?${params.toString()}` : ""}`;
        navigate(newUrl, { replace: true });
    };

    const handleFilterChange = (field, value) => {
        const newFilters = { ...filters, [field]: value };
        setFilters(newFilters);
        updateURL(newFilters);
    };

    const handleSearch = (e) => {
        e.preventDefault();
        // Search is handled by handleFilterChange when typing
    };

    const handleClearFilters = () => {
        const clearedFilters = {
            search: "",
            category: "",
            sortBy: "",
            minPrice: 0,
            maxPrice: 2000000,
        };
        setFilters(clearedFilters);
        updateURL(clearedFilters, 1);
    };

    const handlePageChange = (event, page) => {
        setCurrentPage(page);
        updateURL(filters, page);
        // Scroll to top
        window.scrollTo({ top: 0, behavior: "smooth" });
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(price);
    };

    const getCategoryDisplayName = (category) => {
        switch (category) {
            case "book":
                return "Books";
            case "cd":
                return "CDs";
            case "dvd":
                return "DVDs";
            case "lp":
                return "LPs (Vinyl)";
            default:
                return category;
        }
    };

    // Pagination
    const totalPages = Math.ceil(filteredProducts.length / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const paginatedProducts = filteredProducts.slice(startIndex, startIndex + itemsPerPage);

    if (loading) {
        return <LoadingSpinner message="Loading products..." />;
    }

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            {/* Header */}
            <Box sx={{ mb: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Products
                </Typography>
                <Typography variant="body1" color="text.secondary">
                    Discover our wide selection of books, CDs, DVDs, and vinyl records.
                </Typography>
            </Box>

            {/* Filters */}
            <Card sx={{ mb: 4 }}>
                <CardContent>
                    <Grid container spacing={3} alignItems="center">
                        {/* Search */}
                        <Grid item xs={12} md={4}>
                            <TextField
                                fullWidth
                                placeholder="Search products..."
                                value={filters.search}
                                onChange={(e) => handleFilterChange("search", e.target.value)}
                                InputProps={{
                                    startAdornment: (
                                        <InputAdornment position="start">
                                            <Search />
                                        </InputAdornment>
                                    ),
                                }}
                            />
                        </Grid>

                        {/* Category Filter */}
                        <Grid item xs={12} sm={6} md={2}>
                            <FormControl fullWidth>
                                <InputLabel>Category</InputLabel>
                                <Select value={filters.category} onChange={(e) => handleFilterChange("category", e.target.value)} label="Category">
                                    <MenuItem value="">All Categories</MenuItem>
                                    <MenuItem value="book">Books</MenuItem>
                                    <MenuItem value="cd">CDs</MenuItem>
                                    <MenuItem value="dvd">DVDs</MenuItem>
                                    <MenuItem value="lp">LPs (Vinyl)</MenuItem>
                                </Select>
                            </FormControl>
                        </Grid>

                        {/* Sort By */}
                        <Grid item xs={12} sm={6} md={2}>
                            <FormControl fullWidth>
                                <InputLabel>Sort By</InputLabel>
                                <Select value={filters.sortBy} onChange={(e) => handleFilterChange("sortBy", e.target.value)} label="Sort By">
                                    <MenuItem value="">Default</MenuItem>
                                    <MenuItem value="title_asc">Title A-Z</MenuItem>
                                    <MenuItem value="title_desc">Title Z-A</MenuItem>
                                    <MenuItem value="price_asc">Price Low-High</MenuItem>
                                    <MenuItem value="price_desc">Price High-Low</MenuItem>
                                    <MenuItem value="newest">Newest First</MenuItem>
                                </Select>
                            </FormControl>
                        </Grid>

                        {/* Price Range */}
                        <Grid item xs={12} md={3}>
                            <Typography variant="body2" gutterBottom>
                                Price Range
                            </Typography>
                            <Slider
                                value={[filters.minPrice, filters.maxPrice]}
                                onChange={(e, newValue) => {
                                    handleFilterChange("minPrice", newValue[0]);
                                    handleFilterChange("maxPrice", newValue[1]);
                                }}
                                valueLabelDisplay="auto"
                                valueLabelFormat={(value) => formatPrice(value)}
                                min={0}
                                max={2000000}
                                step={50000}
                            />
                            <Box sx={{ display: "flex", justifyContent: "space-between", mt: 1 }}>
                                <Typography variant="caption">{formatPrice(filters.minPrice)}</Typography>
                                <Typography variant="caption">{formatPrice(filters.maxPrice)}</Typography>
                            </Box>
                        </Grid>

                        {/* Clear Filters */}
                        <Grid item xs={12} md={1}>
                            <Button variant="outlined" startIcon={<Clear />} onClick={handleClearFilters} fullWidth sx={{ minHeight: "56px" }}>
                                Clear
                            </Button>
                        </Grid>
                    </Grid>
                </CardContent>
            </Card>

            {/* Active Filters Display */}
            {(filters.search || filters.category || filters.sortBy || filters.minPrice !== 0 || filters.maxPrice !== 2000000) && (
                <Box sx={{ mb: 3 }}>
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                        Active filters:
                    </Typography>
                    <Box sx={{ display: "flex", gap: 1, flexWrap: "wrap" }}>
                        {filters.search && (
                            <Chip label={`Search: "${filters.search}"`} onDelete={() => handleFilterChange("search", "")} size="small" />
                        )}
                        {filters.category && (
                            <Chip
                                label={`Category: ${getCategoryDisplayName(filters.category)}`}
                                onDelete={() => handleFilterChange("category", "")}
                                size="small"
                            />
                        )}
                        {filters.sortBy && (
                            <Chip
                                label={`Sort: ${filters.sortBy.replace("_", " ")}`}
                                onDelete={() => handleFilterChange("sortBy", "")}
                                size="small"
                            />
                        )}
                        {(filters.minPrice !== 0 || filters.maxPrice !== 2000000) && (
                            <Chip
                                label={`Price: ${formatPrice(filters.minPrice)} - ${formatPrice(filters.maxPrice)}`}
                                onDelete={() => {
                                    handleFilterChange("minPrice", 0);
                                    handleFilterChange("maxPrice", 2000000);
                                }}
                                size="small"
                            />
                        )}
                    </Box>
                </Box>
            )}

            {/* Results Count */}
            <Box sx={{ mb: 3, display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <Typography variant="body1">
                    {filteredProducts.length} product{filteredProducts.length !== 1 ? "s" : ""} found
                    {currentPage > 1 && ` (Page ${currentPage} of ${totalPages})`}
                </Typography>
                {totalPages > 1 && (
                    <Typography variant="body2" color="text.secondary">
                        Showing {startIndex + 1}-{Math.min(startIndex + itemsPerPage, filteredProducts.length)} of {filteredProducts.length}
                    </Typography>
                )}
            </Box>

            {/* Products Grid */}
            {paginatedProducts.length > 0 ? (
                <Grid container spacing={3}>
                    {paginatedProducts.map((product) => (
                        <Grid item xs={12} sm={6} md={4} lg={3} key={product.productID}>
                            <ProductCard product={product} />
                        </Grid>
                    ))}
                </Grid>
            ) : (
                <Box sx={{ textAlign: "center", py: 8 }}>
                    <Typography variant="h6" color="text.secondary" gutterBottom>
                        No products found
                    </Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                        Try adjusting your search criteria or clear filters to see more results.
                    </Typography>
                    <Button variant="outlined" onClick={handleClearFilters}>
                        Clear All Filters
                    </Button>
                </Box>
            )}

            {/* Pagination */}
            {totalPages > 1 && (
                <Box sx={{ display: "flex", justifyContent: "center", mt: 6 }}>
                    <Pagination
                        count={totalPages}
                        page={currentPage}
                        onChange={handlePageChange}
                        color="primary"
                        size="large"
                        showFirstButton
                        showLastButton
                    />
                </Box>
            )}
        </Container>
    );
};

export default ProductsPage;
