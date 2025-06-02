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
} from "@mui/material";
import { Save, Cancel } from "@mui/icons-material";

const ProductEditDialog = ({
    open,
    onClose,
    productData = null,
    mode = "view", // "add", "edit", "view"
    onSave,
}) => {
    const [product, setProduct] = useState({
        productID: "",
        title: "",
        category: "book",
        price: 0,
        value: 0,
        quantity: 0,
        description: "",
        barcode: "",
        dimensions: "",
        weight: 0,
        imageURL: "",
    });

    const [errors, setErrors] = useState({});

    useEffect(() => {
        if (productData) {
            setProduct({ ...productData });
        } else if (mode === "add") {
            // Reset form for new product
            setProduct({
                productID: "",
                title: "",
                category: "book",
                price: 0,
                value: 0,
                quantity: 0,
                description: "",
                barcode: "",
                dimensions: "",
                weight: 0,
                imageURL: "",
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

    const validateForm = () => {
        const newErrors = {};

        if (!product.title?.trim()) {
            newErrors.title = "Product title is required";
        }

        if (!product.price || product.price <= 0) {
            newErrors.price = "Price must be greater than 0";
        }

        if (!product.quantity || product.quantity < 0) {
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
            onSave(product, mode);
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
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Publisher"
                                value={product.publisher || ""}
                                onChange={(e) => handleFieldChange("publisher", e.target.value)}
                                disabled={mode === "view"}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Number of Pages"
                                type="number"
                                value={product.numberOfPages || ""}
                                onChange={(e) => handleFieldChange("numberOfPages", parseInt(e.target.value) || 0)}
                                disabled={mode === "view"}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Language"
                                value={product.language || ""}
                                onChange={(e) => handleFieldChange("language", e.target.value)}
                                disabled={mode === "view"}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Genre"
                                value={product.genre || ""}
                                onChange={(e) => handleFieldChange("genre", e.target.value)}
                                disabled={mode === "view"}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Cover Type"
                                value={product.coverType || ""}
                                onChange={(e) => handleFieldChange("coverType", e.target.value)}
                                disabled={mode === "view"}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                label="ISBN"
                                value={product.isbn || ""}
                                onChange={(e) => handleFieldChange("isbn", e.target.value)}
                                disabled={mode === "view"}
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
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Record Label"
                                value={product.recordLabel || ""}
                                onChange={(e) => handleFieldChange("recordLabel", e.target.value)}
                                disabled={mode === "view"}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Music Type"
                                value={product.musicType || ""}
                                onChange={(e) => handleFieldChange("musicType", e.target.value)}
                                disabled={mode === "view"}
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
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Studio"
                                value={product.studio || ""}
                                onChange={(e) => handleFieldChange("studio", e.target.value)}
                                disabled={mode === "view"}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Runtime"
                                value={product.runtime || ""}
                                onChange={(e) => handleFieldChange("runtime", e.target.value)}
                                disabled={mode === "view"}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Disc Type"
                                value={product.discType || ""}
                                onChange={(e) => handleFieldChange("discType", e.target.value)}
                                disabled={mode === "view"}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Genre"
                                value={product.genre || ""}
                                onChange={(e) => handleFieldChange("genre", e.target.value)}
                                disabled={mode === "view"}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Language"
                                value={product.language || ""}
                                onChange={(e) => handleFieldChange("language", e.target.value)}
                                disabled={mode === "view"}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                label="Subtitles"
                                value={product.subtitle || ""}
                                onChange={(e) => handleFieldChange("subtitle", e.target.value)}
                                disabled={mode === "view"}
                            />
                        </Grid>
                    </>
                );

            case "lp":
                return (
                    <>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Artist"
                                value={product.artist || ""}
                                onChange={(e) => handleFieldChange("artist", e.target.value)}
                                disabled={mode === "view"}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Record Label"
                                value={product.recordLabel || ""}
                                onChange={(e) => handleFieldChange("recordLabel", e.target.value)}
                                disabled={mode === "view"}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Vinyl Size"
                                value={product.vinylSize || ""}
                                onChange={(e) => handleFieldChange("vinylSize", e.target.value)}
                                disabled={mode === "view"}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="RPM"
                                value={product.rpm || ""}
                                onChange={(e) => handleFieldChange("rpm", e.target.value)}
                                disabled={mode === "view"}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Music Type"
                                value={product.musicType || ""}
                                onChange={(e) => handleFieldChange("musicType", e.target.value)}
                                disabled={mode === "view"}
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
                                <MenuItem value="lp">LP (Vinyl)</MenuItem>
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
                        />
                    </Grid>

                    <Grid item xs={12} sm={4}>
                        <TextField
                            fullWidth
                            label="Price (VND) *"
                            type="number"
                            value={product.price}
                            onChange={(e) => handleFieldChange("price", parseFloat(e.target.value) || 0)}
                            error={!!errors.price}
                            helperText={errors.price}
                            disabled={mode === "view"}
                            required
                        />
                    </Grid>

                    <Grid item xs={12} sm={4}>
                        <TextField
                            fullWidth
                            label="Value (VND)"
                            type="number"
                            value={product.value}
                            onChange={(e) => handleFieldChange("value", parseFloat(e.target.value) || 0)}
                            disabled={mode === "view"}
                        />
                    </Grid>

                    <Grid item xs={12} sm={4}>
                        <TextField
                            fullWidth
                            label="Quantity *"
                            type="number"
                            value={product.quantity}
                            onChange={(e) => handleFieldChange("quantity", parseInt(e.target.value) || 0)}
                            error={!!errors.quantity}
                            helperText={errors.quantity}
                            disabled={mode === "view"}
                            required
                        />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            label="Weight (kg)"
                            type="number"
                            value={product.weight}
                            onChange={(e) => handleFieldChange("weight", parseFloat(e.target.value) || 0)}
                            disabled={mode === "view"}
                        />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            label="Dimensions"
                            value={product.dimensions}
                            onChange={(e) => handleFieldChange("dimensions", e.target.value)}
                            disabled={mode === "view"}
                        />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            label="Barcode"
                            value={product.barcode}
                            onChange={(e) => handleFieldChange("barcode", e.target.value)}
                            disabled={mode === "view"}
                        />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            label="Image URL"
                            value={product.imageURL}
                            onChange={(e) => handleFieldChange("imageURL", e.target.value)}
                            disabled={mode === "view"}
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
