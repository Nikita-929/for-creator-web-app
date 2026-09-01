import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { CartProvider } from './context/CartContext'
import Layout from './components/Layout'
import HomePage from './pages/HomePage'
import ShopPage from './pages/ShopPage'
import ProductPage from './pages/ProductPage'
import { ArtisansPage, ArtisanDetailPage } from './pages/ArtisanPages'
import AboutPage from './pages/AboutPage'
import { CartPage, CheckoutPage } from './pages/CartCheckoutPages'
import { LoginPage, RegisterPage, AccountPage } from './pages/AuthAccountPages'
import { JournalPage, JournalPostPage, ContactPage, CustomOrdersPage } from './pages/ContentPages'
import AdminPage from './pages/AdminPage'

const queryClient = new QueryClient()

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <CartProvider>
          <BrowserRouter>
            <Routes>
              <Route element={<Layout />}>
                <Route path="/" element={<HomePage />} />
                <Route path="/shop" element={<ShopPage />} />
                <Route path="/product/:id" element={<ProductPage />} />
                <Route path="/artisans" element={<ArtisansPage />} />
                <Route path="/artisans/:id" element={<ArtisanDetailPage />} />
                <Route path="/about" element={<AboutPage />} />
                <Route path="/cart" element={<CartPage />} />
                <Route path="/checkout" element={<CheckoutPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/account" element={<AccountPage />} />
                <Route path="/journal" element={<JournalPage />} />
                <Route path="/journal/:slug" element={<JournalPostPage />} />
                <Route path="/blog" element={<Navigate to="/journal" replace />} />
                <Route path="/contact" element={<ContactPage />} />
                <Route path="/custom-orders" element={<CustomOrdersPage />} />
                <Route path="/admin/*" element={<AdminPage />} />
              </Route>
            </Routes>
          </BrowserRouter>
        </CartProvider>
      </AuthProvider>
    </QueryClientProvider>
  )
}
