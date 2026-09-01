import { useEffect } from 'react'

export default function Seo({ title, description, jsonLd }) {
  useEffect(() => {
    if (title) document.title = `${title} — For Creators`
    if (description) {
      let tag = document.querySelector('meta[name="description"]')
      if (!tag) {
        tag = document.createElement('meta')
        tag.setAttribute('name', 'description')
        document.head.appendChild(tag)
      }
      tag.setAttribute('content', description)
    }
    let script = document.getElementById('jsonld')
    if (jsonLd) {
      if (!script) {
        script = document.createElement('script')
        script.id = 'jsonld'
        script.type = 'application/ld+json'
        document.head.appendChild(script)
      }
      script.textContent = JSON.stringify(jsonLd)
    }
    return () => {
      script?.remove()
    }
  }, [title, description, jsonLd])
  return null
}
