import { Navigate, NavLink, Route, Routes } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import api from '../api/client'
import { useAuth } from '../context/AuthContext'
import Seo from '../components/Seo'
import { inr } from '../lib/format'

function Field({ label, children }) {
  return (
    <label className="block text-sm">
      {label}
      <div className="mt-1">{children}</div>
    </label>
  )
}

const input = 'w-full border border-sand px-3 py-2 bg-paper'

function ProductsAdmin() {
  const qc = useQueryClient()
  const products = useQuery({ queryKey: ['admin-products'], queryFn: () => api.get('/api/products', { params: { size: 50 } }).then((r) => r.data) })
  const categories = useQuery({ queryKey: ['categories'], queryFn: () => api.get('/api/categories').then((r) => r.data) })
  const artisans = useQuery({ queryKey: ['artisans'], queryFn: () => api.get('/api/artisans').then((r) => r.data) })
  const blank = {
    name: '',
    description: '',
    categoryId: '',
    subcategory: '',
    technique: '',
    materials: '',
    dimensions: '',
    price: '',
    stockQuantity: 1,
    oneOfAKind: false,
    images: '',
    imageAlts: '',
    artisanId: '',
    tags: '',
  }
  const [form, setForm] = useState(blank)
  const [editId, setEditId] = useState(null)

  function payload() {
    return {
      ...form,
      categoryId: form.categoryId ? Number(form.categoryId) : null,
      artisanId: form.artisanId ? Number(form.artisanId) : null,
      price: Number(form.price),
      stockQuantity: Number(form.stockQuantity),
      materials: form.materials.split(',').map((s) => s.trim()).filter(Boolean),
      images: form.images.split(',').map((s) => s.trim()).filter(Boolean),
      imageAlts: form.imageAlts.split(',').map((s) => s.trim()).filter(Boolean),
      tags: form.tags.split(',').map((s) => s.trim()).filter(Boolean),
    }
  }

  const save = useMutation({
    mutationFn: () => (editId ? api.put(`/api/admin/products/${editId}`, payload()) : api.post('/api/admin/products', payload())),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['admin-products'] })
      qc.invalidateQueries({ queryKey: ['products'] })
      setForm(blank)
      setEditId(null)
    },
  })

  return (
    <div>
      <h2 className="font-display text-2xl">Products</h2>
      <form
        className="grid md:grid-cols-2 gap-3 mt-4"
        onSubmit={(e) => {
          e.preventDefault()
          save.mutate()
        }}
      >
        <Field label="Name">
          <input className={input} required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        </Field>
        <Field label="Price (INR)">
          <input className={input} required type="number" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} />
        </Field>
        <Field label="Stock">
          <input className={input} type="number" value={form.stockQuantity} onChange={(e) => setForm({ ...form, stockQuantity: e.target.value })} />
        </Field>
        <Field label="Category">
          <select className={input} value={form.categoryId} onChange={(e) => setForm({ ...form, categoryId: e.target.value })}>
            <option value="">—</option>
            {categories.data?.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>
        </Field>
        <Field label="Artisan">
          <select className={input} value={form.artisanId} onChange={(e) => setForm({ ...form, artisanId: e.target.value })}>
            <option value="">—</option>
            {artisans.data?.map((a) => (
              <option key={a.id} value={a.id}>
                {a.name}
              </option>
            ))}
          </select>
        </Field>
        <Field label="Technique">
          <input className={input} value={form.technique} onChange={(e) => setForm({ ...form, technique: e.target.value })} />
        </Field>
        <Field label="Materials (comma)">
          <input className={input} value={form.materials} onChange={(e) => setForm({ ...form, materials: e.target.value })} />
        </Field>
        <Field label="Dimensions">
          <input className={input} value={form.dimensions} onChange={(e) => setForm({ ...form, dimensions: e.target.value })} />
        </Field>
        <Field label="Image URLs (comma)">
          <input className={input} value={form.images} onChange={(e) => setForm({ ...form, images: e.target.value })} />
        </Field>
        <Field label="Image alt text (comma)">
          <input className={input} value={form.imageAlts} onChange={(e) => setForm({ ...form, imageAlts: e.target.value })} />
        </Field>
        <label className="flex items-center gap-2 text-sm md:col-span-2">
          <input type="checkbox" checked={form.oneOfAKind} onChange={(e) => setForm({ ...form, oneOfAKind: e.target.checked })} />
          One of a kind
        </label>
        <textarea className={`${input} md:col-span-2`} rows="3" placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
        <button className="bg-ink text-cream px-4 py-2 rounded" type="submit">
          {editId ? 'Update' : 'Create'}
        </button>
      </form>
      <ul className="mt-8 divide-y divide-sand">
        {products.data?.content?.map((p) => (
          <li key={p.id} className="py-3 flex justify-between gap-4">
            <span>
              {p.name} — {inr(p.price)} {p.soldOut ? '(sold out)' : ''}
            </span>
            <span className="flex gap-3 text-sm">
              <button
                type="button"
                className="underline"
                onClick={async () => {
                  const full = await api.get(`/api/products/${p.id}`).then((r) => r.data)
                  setEditId(full.id)
                  setForm({
                    name: full.name,
                    description: full.description || '',
                    categoryId: full.categoryId || '',
                    subcategory: full.subcategory || '',
                    technique: full.technique || '',
                    materials: (full.materials || []).join(', '),
                    dimensions: full.dimensions || '',
                    price: full.price,
                    stockQuantity: full.stockQuantity,
                    oneOfAKind: full.oneOfAKind,
                    images: (full.images || []).join(', '),
                    imageAlts: (full.imageAlts || []).join(', '),
                    artisanId: full.artisanId || '',
                    tags: (full.tags || []).join(', '),
                  })
                }}
              >
                Edit
              </button>
              <button
                type="button"
                className="underline text-clay"
                onClick={() => api.delete(`/api/admin/products/${p.id}`).then(() => qc.invalidateQueries({ queryKey: ['admin-products'] }))}
              >
                Delete
              </button>
            </span>
          </li>
        ))}
      </ul>
    </div>
  )
}

function SimpleCrud({ title, listKey, path, fields, mapItem, allowDelete = true }) {
  const qc = useQueryClient()
  const list = useQuery({ queryKey: [listKey], queryFn: () => api.get(path).then((r) => r.data) })
  const initial = Object.fromEntries(fields.map((f) => [f.key, f.type === 'select' ? f.options[0] : '']))
  const [form, setForm] = useState(initial)
  const [editId, setEditId] = useState(null)

  return (
    <div>
      <h2 className="font-display text-2xl">{title}</h2>
      <form
        className="mt-4 space-y-3 max-w-lg"
        onSubmit={async (e) => {
          e.preventDefault()
          if (editId) await api.put(`/api/admin${path}/${editId}`, form)
          else await api.post(`/api/admin${path}`, form)
          qc.invalidateQueries({ queryKey: [listKey] })
          setForm(initial)
          setEditId(null)
        }}
      >
        {fields.map((f) => (
          <Field key={f.key} label={f.label}>
            {f.type === 'textarea' ? (
              <textarea className={input} value={form[f.key] ?? ''} onChange={(e) => setForm({ ...form, [f.key]: e.target.value })} />
            ) : f.type === 'select' ? (
              <select className={input} value={form[f.key] ?? ''} onChange={(e) => setForm({ ...form, [f.key]: e.target.value })}>
                {f.options.map((o) => (
                  <option key={o}>{o}</option>
                ))}
              </select>
            ) : (
              <input className={input} value={form[f.key] ?? ''} onChange={(e) => setForm({ ...form, [f.key]: e.target.value })} />
            )}
          </Field>
        ))}
        <button className="bg-ink text-cream px-4 py-2 rounded" type="submit">
          {editId ? 'Update' : 'Create'}
        </button>
      </form>
      <ul className="mt-6 space-y-2">
        {list.data?.map((item) => (
          <li key={item.id} className="flex justify-between gap-4 border-b border-sand py-2">
            <span>{mapItem(item)}</span>
            <span className="flex gap-3 text-sm">
              <button
                type="button"
                className="underline"
                onClick={() => {
                  setEditId(item.id)
                  setForm({ ...initial, ...item })
                }}
              >
                Edit
              </button>
              {allowDelete && (
                <button
                  type="button"
                  className="underline text-clay"
                  onClick={async () => {
                    await api.delete(`/api/admin${path}/${item.id}`)
                    qc.invalidateQueries({ queryKey: [listKey] })
                  }}
                >
                  Delete
                </button>
              )}
            </span>
          </li>
        ))}
      </ul>
    </div>
  )
}

function OrdersAdmin() {
  const qc = useQueryClient()
  const { data } = useQuery({ queryKey: ['admin-orders'], queryFn: () => api.get('/api/admin/orders').then((r) => r.data) })
  return (
    <div>
      <h2 className="font-display text-2xl">Orders</h2>
      <ul className="mt-4 space-y-3">
        {data?.map((o) => (
          <li key={o.id} className="border border-sand p-3">
            <p>
              #{o.id} {inr(o.total)} — {o.status} / {o.paymentStatus}
            </p>
            <select
              className="mt-2 border border-sand px-2 py-1"
              value={o.status}
              onChange={(e) => api.patch(`/api/admin/orders/${o.id}`, { status: e.target.value }).then(() => qc.invalidateQueries({ queryKey: ['admin-orders'] }))}
            >
              {['PENDING', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED'].map((s) => (
                <option key={s}>{s}</option>
              ))}
            </select>
          </li>
        ))}
      </ul>
    </div>
  )
}

function CustomAdmin() {
  const qc = useQueryClient()
  const { data } = useQuery({ queryKey: ['admin-custom'], queryFn: () => api.get('/api/admin/custom-orders').then((r) => r.data) })
  return (
    <div>
      <h2 className="font-display text-2xl">Custom order requests</h2>
      <ul className="mt-4 space-y-3">
        {data?.map((r) => (
          <li key={r.id} className="border border-sand p-3">
            <p className="font-medium">
              {r.name} — {r.contact}
            </p>
            <p className="text-sm">{r.description}</p>
            <select
              className="mt-2 border px-2 py-1"
              value={r.status}
              onChange={(e) => api.patch(`/api/admin/custom-orders/${r.id}`, { status: e.target.value }).then(() => qc.invalidateQueries({ queryKey: ['admin-custom'] }))}
            >
              {['NEW', 'REVIEWING', 'QUOTED', 'CLOSED'].map((s) => (
                <option key={s}>{s}</option>
              ))}
            </select>
          </li>
        ))}
      </ul>
    </div>
  )
}

function JournalAdmin() {
  return (
    <SimpleCrud
      title="Journal posts"
      listKey="journal"
      path="/journal"
      fields={[
        { key: 'title', label: 'Title' },
        { key: 'slug', label: 'Slug (optional)' },
        { key: 'excerpt', label: 'Excerpt', type: 'textarea' },
        { key: 'coverImage', label: 'Cover image URL' },
        { key: 'body', label: 'Body', type: 'textarea' },
      ]}
      mapItem={(j) => j.title}
    />
  )
}

export default function AdminPage() {
  const { user, ready } = useAuth()
  if (!ready) return <p className="p-8">Loading…</p>
  if (!user || user.role !== 'ADMIN') return <Navigate to="/login" replace />

  const link = ({ isActive }) => (isActive ? 'text-clay' : 'hover:text-clay')

  return (
    <div className="mx-auto max-w-6xl px-4 py-10 grid md:grid-cols-[180px_1fr] gap-8">
      <Seo title="Admin" />
      <nav className="flex md:flex-col gap-3 text-sm" aria-label="Admin">
        <NavLink to="/admin/products" className={link}>
          Products
        </NavLink>
        <NavLink to="/admin/categories" className={link}>
          Categories
        </NavLink>
        <NavLink to="/admin/artisans" className={link}>
          Artisans
        </NavLink>
        <NavLink to="/admin/journal" className={link}>
          Journal
        </NavLink>
        <NavLink to="/admin/orders" className={link}>
          Orders
        </NavLink>
        <NavLink to="/admin/custom-orders" className={link}>
          Custom orders
        </NavLink>
      </nav>
      <Routes>
        <Route index element={<Navigate to="products" replace />} />
        <Route path="products" element={<ProductsAdmin />} />
        <Route
          path="categories"
          element={
            <SimpleCrud
              title="Categories"
              listKey="categories"
              path="/categories"
              fields={[
                { key: 'name', label: 'Name' },
                { key: 'type', label: 'Type', type: 'select', options: ['TRADITIONAL', 'MODERN'] },
                { key: 'parentId', label: 'Parent category ID (optional)' },
              ]}
              mapItem={(c) => `${c.name} (${c.type})`}
            />
          }
        />
        <Route
          path="artisans"
          element={
            <SimpleCrud
              title="Artisans"
              listKey="artisans"
              path="/artisans"
              fields={[
                { key: 'name', label: 'Name' },
                { key: 'workshopLocation', label: 'Workshop' },
                { key: 'photo', label: 'Photo URL' },
                { key: 'bio', label: 'Bio', type: 'textarea' },
                { key: 'processDescription', label: 'Process', type: 'textarea' },
              ]}
              mapItem={(a) => a.name}
            />
          }
        />
        <Route path="journal" element={<JournalAdmin />} />
        <Route path="orders" element={<OrdersAdmin />} />
        <Route path="custom-orders" element={<CustomAdmin />} />
      </Routes>
    </div>
  )
}
