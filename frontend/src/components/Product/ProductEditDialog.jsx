import React, { useState, useEffect } from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Grid,
    Button,
    Typography,
    Alert,
    Checkbox,
    FormControlLabel,
    Autocomplete,
} from "@mui/material";
import { Save, Cancel } from "@mui/icons-material";
import DailyLimitsInfo from "../Common/DailyLimitsInfo";

const ProductEditDialog = ({
    open,
    onClose,
    productData = null,
    mode = "view", // "add", "edit", "view"
    onSave,
}) => {
    // Smart dropdown options
    const languageOptions = [
        "English", "Vietnamese", "French", "Spanish", "German", "Italian", 
        "Japanese", "Korean", "Chinese", "Russian", "Portuguese", "Arabic"
    ];

    const bookGenreOptions = [
        "Fiction", "Non-Fiction", "Mystery", "Romance", "Sci-Fi", "Fantasy", 
        "Biography", "History", "Self-Help", "Travel", "Poetry", "Drama", 
        "Thriller", "Horror", "Children's", "Young Adult"
    ];

    const coverTypeOptions = [
        "Hardcover", "Paperback", "Mass Market Paperback", "Board Book", "Spiral Bound"
    ];

    const musicTypeOptions = [
        "Rock", "Pop", "Jazz", "Classical", "Electronic", "Hip-Hop", "R&B", 
        "Country", "Folk", "Blues", "Reggae", "Metal", "Punk", "Alternative", 
        "K-Pop", "Latin", "Instrumental"
    ];

    const discTypeOptions = [
        "DVD", "Blu-ray", "4K Ultra HD", "Digital"
    ];

    const movieGenreOptions = [
        "Action", "Adventure", "Comedy", "Drama", "Horror", "Thriller", 
        "Sci-Fi", "Fantasy", "Romance", "Documentary", "Animation", "Musical", 
        "War", "Western", "Crime", "Family"
    ];

    const [product, setProduct] = useState({
        productID: null,
        title: "",
        category: "book",
        price: "",
        value: "",
        quantity: "",
        description: "",
        barcode: "",
        dimensions: "",
        weight: "",
        imageURL: "",
        warehouseEntryDate: "",
        eligible: false,
    });

    const [errors, setErrors] = useState({});

    useEffect(() => {
        if (productData) {
            setProduct({ 
                ...productData,
                // Convert numbers to strings for display, handle null/undefined
                price: productData.price !== null && productData.price !== undefined ? productData.price.toString() : "",
                value: productData.value !== null && productData.value !== undefined ? productData.value.toString() : "",
                quantity: productData.quantity !== null && productData.quantity !== undefined ? productData.quantity.toString() : "",
                weight: productData.weight !== null && productData.weight !== undefined ? productData.weight.toString() : "",
                numberOfPages: productData.numberOfPages !== null && productData.numberOfPages !== undefined ? productData.numberOfPages.toString() : "",
            });
        } else if (mode === "add") {
            // Reset form for new product
            setProduct({
                productID: null,
                title: "",
                category: "book",
                price: "",
                value: "",
                quantity: "",
                description: "",
                barcode: "",
                dimensions: "",
                weight: "",
                imageURL: "",
                warehouseEntryDate: "",
                eligible: false,
            });
        }
        setErrors({});
    }, [productData, mode, open]);

    const handleFieldChange = (field, value) => {
        setProduct((prev) => ({
            ...prev,
            [field]: value,
        }));

        // Clear error when user starts typing
        if (errors[field]) {
            setErrors((prev) => ({
                ...prev,
                [field]: "",
            }));
        }
    };

    const handleNumberChange = (field, value) => {
        // Allow empty string or valid numbers (including 0)
        if (value === "" || (!isNaN(value) && !isNaN(parseFloat(value)))) {
            handleFieldChange(field, value);
        }
    };

    const validateForm = () => {
        const newErrors = {};

        if (!product.title?.trim()) {
            newErrors.title = "Product title is required";
        }

        const price = parseFloat(product.price);
        if (!product.price || isNaN(price) || price <= 0) {
            newErrors.price = "Price must be greater than 0";
        }

        const quantity = parseInt(product.quantity);
        if (product.quantity === "" || isNaN(quantity) || quantity < 0) {
            newErrors.quantity = "Quantity must be 0 or greater";
        }

        if (!product.category) {
            newErrors.category = "Category is required";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSave = () => {
        if (validateForm()) {
            // Convert string numbers back to numbers for saving
            const productData = {
                ...product,
                productID: mode === "add" ? null : product.productID,
                price: parseFloat(product.price) || 0,
                value: parseFloat(product.value) || 0,
                quantity: parseInt(product.quantity) || 0,
                weight: parseFloat(product.weight) || 0,
                numberOfPages: parseInt(product.numberOfPages) || 0,
            };
            
            onSave(productData, mode);
        }
    };

    const renderCategoryFields = () => {
        switch (product.category) {
            case "book":
                return (
                    <>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Author(s)"
                                value={product.authors || ""}
                                onChange={(e) => handleFieldChange("authors", e.target.value)}
                                disabled={mode === "view"}
                                placeholder="e.g., Jane Doe, John Smith"
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Publisher"
                                value={product.publisher || ""}
                                onChange={(e) => handleFieldChange("publisher", e.target.value)}
                                disabled={mode === "view"}
                                placeholder="e.g., Penguin Random House"
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Number of Pages"
                                type="number"
                                value={product.numberOfPages || ""}
                                onChange={(e) => handleNumberChange("numberOfPages", e.target.value)}
                                disabled={mode === "view"}
                                inputProps={{ min: 0 }}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <Autocomplete
                                options={languageOptions}
                                value={product.language || ""}
                                onChange={(event, newValue) => handleFieldChange("language", newValue || "")}
                                disabled={mode === "view"}
                                freeSolo
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        fullWidth
                                        label="Language"
                                        placeholder="Select or type language"
                                    />
                                )}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <Autocomplete
                                options={bookGenreOptions}
                                value={product.genre || ""}
                                onChange={(event, newValue) => handleFieldChange("genre", newValue || "")}
                                disabled={mode === "view"}
                                freeSolo
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        fullWidth
                                        label="Genre"
                                        placeholder="Select or type genre"
                                    />
                                )}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <Autocomplete
                                options={coverTypeOptions}
                                value={product.coverType || ""}
                                onChange={(event, newValue) => handleFieldChange("coverType", newValue || "")}
                                disabled={mode === "view"}
                                freeSolo
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        fullWidth
                                        label="Cover Type"
                                        placeholder="Select or type cover type"
                                    />
                                )}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                label="Publication Date"
                                type="date"
                                value={product.pubDate ? new Date(product.pubDate).toISOString().split("T")[0] : ""}
                                onChange={(e) => handleFieldChange("pubDate", e.target.value)}
                                disabled={mode === "view"}
                                InputLabelProps={{ shrink: true }}
                            />
                        </Grid>
                    </>
                );

            case "cd":
                return (
                    <>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Artist"
                                value={product.artist || ""}
                                onChange={(e) => handleFieldChange("artist", e.target.value)}
                                disabled={mode === "view"}
                                placeholder="e.g., The Beatles"
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Record Label"
                                value={product.recordLabel || ""}
                                onChange={(e) => handleFieldChange("recordLabel", e.target.value)}
                                disabled={mode === "view"}
                                placeholder="e.g., Universal Music"
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <Autocomplete
                                options={musicTypeOptions}
                                value={product.musicType || ""}
                                onChange={(event, newValue) => handleFieldChange("musicType", newValue || "")}
                                disabled={mode === "view"}
                                freeSolo
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        fullWidth
                                        label="Music Type"
                                        placeholder="Select or type music type"
                                    />
                                )}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Release Date"
                                type="date"
                                value={product.releaseDate ? new Date(product.releaseDate).toISOString().split("T")[0] : ""}
                                onChange={(e) => handleFieldChange("releaseDate", e.target.value)}
                                disabled={mode === "view"}
                                InputLabelProps={{ shrink: true }}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                label="Tracklist"
                                multiline
                                rows={3}
                                value={product.tracklist || ""}
                                onChange={(e) => handleFieldChange("tracklist", e.target.value)}
                                disabled={mode === "view"}
                                placeholder="e.g., Track 1, Track 2, Track 3..."
                            />
                        </Grid>
                    </>
                );

            case "dvd":
                return (
                    <>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Director"
                                value={product.director || ""}
                                onChange={(e) => handleFieldChange("director", e.target.value)}
                                disabled={mode === "view"}
                                placeholder="e.g., Steven Spielberg"
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Studio"
                                value={product.studio || ""}
                                onChange={(e) => handleFieldChange("studio", e.target.value)}
                                disabled={mode === "view"}
                                placeholder="e.g., Warner Bros"
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Runtime"
                                value={product.runtime || ""}
                                onChange={(e) => handleFieldChange("runtime", e.target.value)}
                                disabled={mode === "view"}
                                placeholder="e.g., 2h 30m"
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <Autocomplete
                                options={discTypeOptions}
                                value={product.discType || ""}
                                onChange={(event, newValue) => handleFieldChange("discType", newValue || "")}
                                disabled={mode === "view"}
                                freeSolo
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        fullWidth
                                        label="Disc Type"
                                        placeholder="Select or type disc type"
                                    />
                                )}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <Autocomplete
                                options={movieGenreOptions}
                                value={product.genre || ""}
                                onChange={(event, newValue) => handleFieldChange("genre", newValue || "")}
                                disabled={mode === "view"}
                                freeSolo
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        fullWidth
                                        label="Genre"
                                        placeholder="Select or type genre"
                                    />
                                )}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <Autocomplete
                                options={languageOptions}
                                value={product.language || ""}
                                onChange={(event, newValue) => handleFieldChange("language", newValue || "")}
                                disabled={mode === "view"}
                                freeSolo
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        fullWidth
                                        label="Language"
                                        placeholder="Select or type language"
                                    />
                                )}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                label="Subtitle"
                                value={product.subtitle || ""}
                                onChange={(e) => handleFieldChange("subtitle", e.target.value)}
                                disabled={mode === "view"}
                                placeholder="e.g., English, Spanish, French"
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                label="Release Date"
                                type="date"
                                value={product.releaseDate ? new Date(product.releaseDate).toISOString().split("T")[0] : ""}
                                onChange={(e) => handleFieldChange("releaseDate", e.target.value)}
                                disabled={mode === "view"}
                                InputLabelProps={{ shrink: true }}
                            />
                        </Grid>
                    </>
                );

            default:
                return null;
        }
    };

    const getDialogTitle = () => {
        switch (mode) {
            case "add":
                return "Add New Product";
            case "edit":
                return "Edit Product";
            case "view":
                return "Product Details";
            default:
                return "Product";
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth PaperProps={{ sx: { minHeight: "70vh" } }}>
            <DialogTitle>{getDialogTitle()}</DialogTitle>
            <DialogContent>
                <Grid container spacing={2} sx={{ mt: 1 }}>
                    {/* Error Display */}
                    {Object.keys(errors).length > 0 && (
                        <Grid item xs={12}>
                            <Alert severity="error">
                                Please fix the following errors:
                                <ul style={{ marginBottom: 0 }}>
                                    {Object.entries(errors).map(([field, error]) => (
                                        <li key={field}>{error}</li>
                                    ))}
                                </ul>
                            </Alert>
                        </Grid>
                    )}

                    {/* Daily Limits Warning */}
                    <Grid item xs={12}>
                        <DailyLimitsInfo mode={mode} />
                    </Grid>

                    {/* Basic Information */}
                    <Grid item xs={12}>
                        <Typography variant="h6" gutterBottom>
                            Basic Information
                        </Typography>
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            label="Product Title *"
                            value={product.title}
                            onChange={(e) => handleFieldChange("title", e.target.value)}
                            error={!!errors.title}
                            helperText={errors.title}
                            disabled={mode === "view"}
                            required
                            placeholder="Enter product title"
                        />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <FormControl fullWidth error={!!errors.category}>
                            <InputLabel>Category *</InputLabel>
                            <Select
                                value={product.category}
                                onChange={(e) => handleFieldChange("category", e.target.value)}
                                label="Category *"
                                disabled={mode === "view"}
                                required
                            >
                                <MenuItem value="book">Book</MenuItem>
                                <MenuItem value="cd">CD</MenuItem>
                                <MenuItem value="dvd">DVD</MenuItem>
                            </Select>
                        </FormControl>
                    </Grid>

                    <Grid item xs={12}>
                        <TextField
                            fullWidth
                            label="Description"
                            multiline
                            rows={3}
                            value={product.description}
                            onChange={(e) => handleFieldChange("description", e.target.value)}
                            disabled={mode === "view"}
                            placeholder="Enter product description"
                        />
                    </Grid>

                    <Grid item xs={12} sm={4}>
                        <TextField
                            fullWidth
                            label="Price (VND) *"
                            type="number"
                            value={product.price}
                            onChange={(e) => handleNumberChange("price", e.target.value)}
                            error={!!errors.price}
                            helperText={
                                errors.price || 
                                (mode === "edit" && product.value ? 
                                    `Must be between ${Math.round(product.value * 0.3).toLocaleString()} and ${Math.round(product.value * 1.5).toLocaleString()} VND (30%-150% of value)` : 
                                    "Enter product price"
                                )
                            }
                            disabled={mode === "view"}
                            required
                            inputProps={{ min: 0, step: 0.01 }}
                            placeholder="0.00"
                        />
                    </Grid>

                    <Grid item xs={12} sm={4}>
                        <TextField
                            fullWidth
                            label="Value (VND)"
                            type="number"
                            value={product.value}
                            onChange={(e) => handleNumberChange("value", e.target.value)}
                            disabled={mode === "view"}
                            inputProps={{ min: 0, step: 0.01 }}
                            placeholder="0.00"
                            helperText="Base value for price validation"
                        />
                    </Grid>

                    <Grid item xs={12} sm={4}>
                        <TextField
                            fullWidth
                            label="Quantity *"
                            type="number"
                            value={product.quantity}
                            onChange={(e) => handleNumberChange("quantity", e.target.value)}
                            error={!!errors.quantity}
                            helperText={errors.quantity}
                            disabled={mode === "view"}
                            required
                            inputProps={{ min: 0 }}
                            placeholder="0"
                        />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            label="Weight (kg)"
                            type="number"
                            value={product.weight}
                            onChange={(e) => handleNumberChange("weight", e.target.value)}
                            disabled={mode === "view"}
                            inputProps={{ min: 0, step: 0.01 }}
                            placeholder="0.00"
                        />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            label="Dimensions"
                            value={product.dimensions}
                            onChange={(e) => handleFieldChange("dimensions", e.target.value)}
                            disabled={mode === "view"}
                            placeholder="e.g., 20x13x3 cm"
                        />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            label="Barcode"
                            value={product.barcode}
                            onChange={(e) => handleFieldChange("barcode", e.target.value)}
                            disabled={mode === "view"}
                            placeholder="e.g., 9781234567890"
                        />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            label="Image URL"
                            value={product.imageURL}
                            onChange={(e) => handleFieldChange("imageURL", e.target.value)}
                            disabled={mode === "view"}
                            placeholder="https://example.com/image.jpg"
                        />
                    </Grid>

                    <Grid item xs={12}>
                        <TextField
                            fullWidth
                            label="Warehouse Entry Date"
                            type="date"
                            value={product.warehouseEntryDate ? new Date(product.warehouseEntryDate).toISOString().split("T")[0] : ""}
                            onChange={(e) => handleFieldChange("warehouseEntryDate", e.target.value)}
                            disabled={mode === "view"}
                            InputLabelProps={{ shrink: true }}
                        />
                    </Grid>

                    <Grid item xs={12}>
                        <FormControlLabel
                            control={
                                <Checkbox
                                    checked={product.eligible || false}
                                    onChange={(e) => handleFieldChange("eligible", e.target.checked)}
                                    disabled={mode === "view"}
                                />
                            }
                            label="Eligible for Rush Delivery"
                        />
                    </Grid>

                    {/* Category Specific Fields */}
                    <Grid item xs={12}>
                        <Typography variant="h6" gutterBottom sx={{ mt: 2 }}>
                            Category Specific Information
                        </Typography>
                    </Grid>

                    {renderCategoryFields()}
                </Grid>
            </DialogContent>

            <DialogActions>
                <Button onClick={onClose} startIcon={<Cancel />}>
                    {mode === "view" ? "Close" : "Cancel"}
                </Button>

                {mode !== "view" && (
                    <Button onClick={handleSave} variant="contained" startIcon={<Save />}>
                        {mode === "add" ? "Add Product" : "Save Changes"}
                    </Button>
                )}
            </DialogActions>
        </Dialog>
    );
};

export default ProductEditDialog;
