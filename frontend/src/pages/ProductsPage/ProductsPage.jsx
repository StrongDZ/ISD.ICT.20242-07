import React, { useState, useEffect } from "react";
import {
    Box,
    Container,
    Typography,
    Grid,
    Card,
    CardContent,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    TextField,
    Button,
    Chip,
    Pagination,
    Alert,
    CircularProgress,
    Stack,
} from "@mui/material";
import { Search, Clear } from "@mui/icons-material";
import { useSearchParams, useNavigate } from "react-router-dom";
import ProductCard from "../../components/Product/ProductCard";
import { productService } from "../../services/productService";

const ProductsPage = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const navigate = useNavigate();

    // State management
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Pagination state
    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize] = useState(20); // Items per page
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    // Filter state
    const [filters, setFilters] = useState({
        category: searchParams.get("category") || "all",
        sortBy: searchParams.get("sortBy") || "",
        minPrice: searchParams.get("minPrice") || "",
        maxPrice: searchParams.get("maxPrice") || "",
        search: searchParams.get("search") || "",
    });

    const categories = [
        { value: "all", label: "All Categories" },
        { value: "book", label: "Books" },
        { value: "cd", label: "CDs" },
        { value: "dvd", label: "DVDs" },
    ];

    const sortOptions = [
        { value: "", label: "Default" },
        { value: "title_asc", label: "Title A-Z" },
        { value: "title_desc", label: "Title Z-A" },
        { value: "price_asc", label: "Price Low to High" },
        { value: "price_desc", label: "Price High to Low" },
    ];

    // Load products with pagination via unified backend endpoint
    const loadProducts = async (page = 0) => {
        try {
            setLoading(true);
            setError(null);

            // Get current filters from URL params to ensure we always use the latest values
            const currentFilters = {
                category: searchParams.get("category") || "all",
                sortBy: searchParams.get("sortBy") || "",
                minPrice: searchParams.get("minPrice") || "",
                maxPrice: searchParams.get("maxPrice") || "",
                search: searchParams.get("search") || "",
            };

            console.log("Loading products with filters:", currentFilters, "page:", page);

            const response = await productService.fetchProducts(currentFilters, page, pageSize);

            if (response.content) {
                // Paginated response from backend
                setProducts(response.content);
                setTotalPages(response.totalPages);
                setTotalElements(response.totalElements);
            } else if (Array.isArray(response)) {
                // Fallback if backend returns list (shouldn't happen but keep old behavior)
                setProducts(response);
                setTotalPages(1);
                setTotalElements(response.length);
            } else {
                setProducts([]);
                setTotalPages(0);
                setTotalElements(0);
            }
        } catch (err) {
            console.error("Error loading products:", err);
            setError(err.message);
            setProducts([]);
        } finally {
            setLoading(false);
        }
    };

    // Update URL with current filters and page
    const updateURL = (newFilters, newPage = 1) => {
        const params = new URLSearchParams();

        Object.entries(newFilters).forEach(([key, value]) => {
            if (value && value !== "all" && value !== "") {
                params.set(key, value);
            }
        });

        if (newPage > 1) {
            params.set("page", newPage.toString());
        }

        setSearchParams(params);
    };

    // Handle filter changes
    const handleFilterChange = (field, value) => {
        const newFilters = { ...filters, [field]: value };
        setFilters(newFilters);
        setCurrentPage(1); // Reset to first page
        updateURL(newFilters, 1);
    };

    // Handle search
    const handleSearch = (e) => {
        e.preventDefault();
        setCurrentPage(1);
        updateURL(filters, 1);
    };

    // Clear all filters
    const handleClearFilters = () => {
        const clearedFilters = {
            category: "all",
            sortBy: "",
            minPrice: "",
            maxPrice: "",
            search: "",
        };
        setFilters(clearedFilters);
        setCurrentPage(1);
        setSearchParams({});
    };

    // Handle page change
    const handlePageChange = (event, page) => {
        setCurrentPage(page);
        updateURL(filters, page);
        window.scrollTo({ top: 0, behavior: "smooth" });
    };

    // Format price for display
    const formatPrice = (price) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(price);
    };

    // Get category display name
    const getCategoryDisplayName = (category) => {
        const categoryObj = categories.find((cat) => cat.value === category);
        return categoryObj ? categoryObj.label : category;
    };

    // Load products when component mounts or filters change
    useEffect(() => {
        const pageFromURL = parseInt(searchParams.get("page")) || 1;
        setCurrentPage(pageFromURL);
        loadProducts(pageFromURL - 1); // Convert to 0-based for backend
    }, [searchParams]); // Only depend on searchParams to avoid circular dependencies

    // Update filters from URL params when searchParams change
    useEffect(() => {
        const filtersFromURL = {
            category: searchParams.get("category") || "all",
            sortBy: searchParams.get("sortBy") || "",
            minPrice: searchParams.get("minPrice") || "",
            maxPrice: searchParams.get("maxPrice") || "",
            search: searchParams.get("search") || "",
        };
        setFilters(filtersFromURL);
    }, [searchParams]);

    return (
        <Container maxWidth="xl" sx={{ py: 4 }}>
            {/* Header */}
            <Box sx={{ mb: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
                    Products
                </Typography>
                {totalElements > 0 && (
                    <Typography variant="body1" color="text.secondary">
                        Showing {(currentPage - 1) * pageSize + 1} - {Math.min(currentPage * pageSize, totalElements)} of {totalElements} products
                    </Typography>
                )}
            </Box>

            {/* Filters */}
            <Card sx={{ mb: 4 }}>
                <CardContent>
                    <Grid container spacing={3} alignItems="center">
                        {/* Search */}
                        <Grid item xs={12} md={4}>
                            <Box component="form" onSubmit={handleSearch} sx={{ display: "flex", gap: 1 }}>
                                <TextField
                                    fullWidth
                                    size="small"
                                    placeholder="Search products..."
                                    value={filters.search}
                                    onChange={(e) => handleFilterChange("search", e.target.value)}
                                    InputProps={{
                                        startAdornment: <Search sx={{ mr: 1, color: "text.secondary" }} />,
                                    }}
                                />
                                <Button type="submit" variant="outlined" size="small">
                                    Search
                                </Button>
                            </Box>
                        </Grid>

                        {/* Category Filter */}
                        <Grid item xs={12} sm={6} md={2}>
                            <FormControl fullWidth size="small">
                                <InputLabel>Category</InputLabel>
                                <Select value={filters.category} label="Category" onChange={(e) => handleFilterChange("category", e.target.value)}>
                                    {categories.map((category) => (
                                        <MenuItem key={category.value} value={category.value}>
                                            {category.label}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>

                        {/* Sort Filter */}
                        <Grid item xs={12} sm={6} md={2}>
                            <FormControl fullWidth size="small">
                                <InputLabel>Sort By</InputLabel>
                                <Select value={filters.sortBy} label="Sort By" onChange={(e) => handleFilterChange("sortBy", e.target.value)}>
                                    {sortOptions.map((option) => (
                                        <MenuItem key={option.value} value={option.value}>
                                            {option.label}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>

                        {/* Price Range */}
                        <Grid item xs={6} md={1.5}>
                            <TextField
                                fullWidth
                                size="small"
                                type="number"
                                placeholder="Min Price"
                                value={filters.minPrice}
                                onChange={(e) => handleFilterChange("minPrice", e.target.value)}
                            />
                        </Grid>
                        <Grid item xs={6} md={1.5}>
                            <TextField
                                fullWidth
                                size="small"
                                type="number"
                                placeholder="Max Price"
                                value={filters.maxPrice}
                                onChange={(e) => handleFilterChange("maxPrice", e.target.value)}
                            />
                        </Grid>

                        {/* Clear Filters */}
                        <Grid item xs={12} md={1}>
                            <Button fullWidth variant="outlined" size="small" startIcon={<Clear />} onClick={handleClearFilters}>
                                Clear
                            </Button>
                        </Grid>
                    </Grid>

                    {/* Active Filters */}
                    <Box sx={{ mt: 2, display: "flex", flexWrap: "wrap", gap: 1 }}>
                        {filters.category !== "all" && (
                            <Chip
                                label={`Category: ${getCategoryDisplayName(filters.category)}`}
                                onDelete={() => handleFilterChange("category", "all")}
                                size="small"
                            />
                        )}
                        {filters.search && (
                            <Chip label={`Search: ${filters.search}`} onDelete={() => handleFilterChange("search", "")} size="small" />
                        )}
                        {filters.minPrice && (
                            <Chip label={`Min: ${formatPrice(filters.minPrice)}`} onDelete={() => handleFilterChange("minPrice", "")} size="small" />
                        )}
                        {filters.maxPrice && (
                            <Chip label={`Max: ${formatPrice(filters.maxPrice)}`} onDelete={() => handleFilterChange("maxPrice", "")} size="small" />
                        )}
                    </Box>
                </CardContent>
            </Card>

            {/* Error Display */}
            {error && (
                <Alert severity="error" sx={{ mb: 4 }}>
                    {error}
                </Alert>
            )}

            {/* Loading State */}
            {loading && (
                <Box sx={{ display: "flex", justifyContent: "center", py: 8 }}>
                    <CircularProgress />
                </Box>
            )}

            {/* Products Grid */}
            {!loading && !error && (
                <>
                    {products.length === 0 ? (
                        <Box sx={{ textAlign: "center", py: 8 }}>
                            <Typography variant="h6" color="text.secondary">
                                No products found
                            </Typography>
                            <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                                Try adjusting your filters or search terms
                            </Typography>
                        </Box>
                    ) : (
                        <>
                            <Grid container spacing={3}>
                                {products.map((product) => (
                                    <Grid item key={product.productID} xs={12} sm={6} md={4} lg={3}>
                                        <ProductCard product={product} />
                                    </Grid>
                                ))}
                            </Grid>

                            {/* Pagination */}
                            {totalPages > 1 && (
                                <Box sx={{ display: "flex", justifyContent: "center", mt: 6 }}>
                                    <Stack spacing={2}>
                                        <Pagination
                                            count={totalPages}
                                            page={currentPage}
                                            onChange={(event, page) => handlePageChange(event, page)}
                                            color="primary"
                                            size="large"
                                            showFirstButton
                                            showLastButton
                                        />
                                        <Typography variant="body2" color="text.secondary" align="center">
                                            Page {currentPage} of {totalPages} ({totalElements} total products)
                                        </Typography>
                                    </Stack>
                                </Box>
                            )}
                        </>
                    )}
                </>
            )}
        </Container>
    );
};

export default ProductsPage;
