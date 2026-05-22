import 'package:flutter/material.dart';
import 'login_screen.dart';
import '../../services/api_service.dart';
import 'dart:io';
import 'package:image_picker/image_picker.dart';

class ProfileScreen extends StatefulWidget {
  final Map<String, dynamic> user;
  final Map<String, dynamic>? participant;
  final bool isOwner;
  final int loggedUserId;

  const ProfileScreen({
    super.key,
    required this.user,
    required this.isOwner,
    required this.loggedUserId,
    this.participant,
  });

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  Map<String, dynamic>? _profile;
  bool _loading = true;
  bool _saving = false;
  bool _editMode = false;

  // Controllers para campos simples
  late TextEditingController _usernameCtrl;
  late TextEditingController _bioCtrl;
  late TextEditingController _callStyleCtrl;
  late TextEditingController _countryCtrl;
  late TextEditingController _timezoneCtrl;

  // Listas editables
  List<String> _games = [];
  List<String> _platforms = [];
  List<String> _languages = [];

  File? _newPhoto;

  // Controllers para añadir items
  final _gameInputCtrl = TextEditingController();
  final _platformInputCtrl = TextEditingController();
  final _languageInputCtrl = TextEditingController();

  @override
  void initState() {
    super.initState();
    _usernameCtrl = TextEditingController();
    _bioCtrl = TextEditingController();
    _callStyleCtrl = TextEditingController();
    _countryCtrl = TextEditingController();
    _timezoneCtrl = TextEditingController();
    _loadProfile();
  }

  @override
  void dispose() {
    _usernameCtrl.dispose();
    _bioCtrl.dispose();
    _callStyleCtrl.dispose();
    _countryCtrl.dispose();
    _timezoneCtrl.dispose();
    _gameInputCtrl.dispose();
    _platformInputCtrl.dispose();
    _languageInputCtrl.dispose();
    super.dispose();
  }

  Future<void> _loadProfile() async {
    final result = await ApiService.getProfile(widget.user['idUser']);
    if (result != null) {
      _fillControllers(result);
    }
    setState(() {
      _profile = result;
      _loading = false;
    });
  }

  void _fillControllers(Map<String, dynamic> profile) {
    _usernameCtrl.text = profile['username'] ?? '';
    _bioCtrl.text = profile['bio'] ?? '';
    _callStyleCtrl.text = profile['callStyle'] ?? '';
    _countryCtrl.text = profile['country'] ?? '';
    _timezoneCtrl.text = profile['timezone'] ?? '';
    _games = (profile['games'] as List? ?? [])
        .take(4)
        .map((g) => g['gameName']?.toString() ?? '')
        .where((g) => g.isNotEmpty)
        .toList();
    _platforms = (profile['platforms'] as List? ?? [])
        .map((p) => p['platform']?.toString() ?? '')
        .where((p) => p.isNotEmpty)
        .toList();
    _languages = (profile['languages'] as List? ?? [])
        .map((l) => l['language']?.toString() ?? '')
        .where((l) => l.isNotEmpty)
        .toList();
  }

  void _enterEditMode() {
    setState(() => _editMode = true);
  }

  void _cancelEdit() {
    _fillControllers(_profile!);
    setState(() => _editMode = false);
  }

  Future<void> _saveProfile() async {
    setState(() => _saving = true);

    final data = {
      'username': _usernameCtrl.text.trim(),
      'bio': _bioCtrl.text.trim(),
      'callStyle': _callStyleCtrl.text.trim(),
      'country': _countryCtrl.text.trim(),
      'timezone': _timezoneCtrl.text.trim(),
      'games': _games.toList(),
      'platforms': _platforms.toList(),
      'languages': _languages.toList(),
    };

    final updated = await ApiService.updateProfile(widget.user['idUser'], data);

    setState(() {
      _saving = false;
      if (updated != null) {
        _profile = updated;
        _fillControllers(updated);
        _editMode = false;
      }
    });

    if (updated == null && mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Error al guardar, inténtalo de nuevo'),
          backgroundColor: Colors.red,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF0F0F13),
      appBar: AppBar(
        backgroundColor: const Color(0xFF1A1A2E),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.white),
          onPressed: () {
            if (_editMode) {
              _cancelEdit();
            } else {
              Navigator.pop(context);
            }
          },
        ),
        title: Text(
          widget.isOwner ? 'Mi Perfil' : '@${widget.user['username'] ?? ''}',
          style: const TextStyle(color: Colors.white),
        ),
        actions: [
          if (widget.isOwner && !_loading) ...[
            if (_editMode) ...[
              if (_saving)
                const Padding(
                  padding: EdgeInsets.all(14),
                  child: SizedBox(
                    width: 20,
                    height: 20,
                    child: CircularProgressIndicator(
                      color: Colors.white,
                      strokeWidth: 2,
                    ),
                  ),
                )
              else ...[
                TextButton(
                  onPressed: _cancelEdit,
                  child: const Text(
                    'Cancelar',
                    style: TextStyle(color: Colors.grey),
                  ),
                ),
                TextButton(
                  onPressed: _saveProfile,
                  child: const Text(
                    'Guardar',
                    style: TextStyle(color: Color(0xFF7C3AED)),
                  ),
                ),
              ],
            ] else
              IconButton(
                icon: const Icon(Icons.edit, color: Colors.white),
                onPressed: _enterEditMode,
              ),
          ],
        ],
      ),
      body: _loading
          ? const Center(
        child: CircularProgressIndicator(color: Color(0xFF7C3AED)),
      )
          : _profile == null
          ? const Center(
        child: Text(
          'No se pudo cargar el perfil',
          style: TextStyle(color: Colors.grey),
        ),
      )
          : _buildBody(),
    );
  }

  Widget _buildBody() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // ── Avatar + nombre + username ──
          Container(
            width: double.infinity,
            padding: const EdgeInsets.all(24),
            decoration: BoxDecoration(
              color: const Color(0xFF1A1A2E),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Column(
              children: [
                GestureDetector(
  onTap: widget.isOwner ? _pickAndUploadPhoto : null,
  child: Stack(
    children: [
      Container(
        width: 96,
        height: 96,
        decoration: BoxDecoration(
          shape: BoxShape.circle,
          color: const Color(0xFF7C3AED),
          image: _newPhoto != null
              ? DecorationImage(
                  image: FileImage(_newPhoto!),
                  fit: BoxFit.cover,
                )
              : (_profile!['photoUrl'] != null
                  ? DecorationImage(
                      image: NetworkImage(_profile!['photoUrl']),
                      fit: BoxFit.cover,
                    )
                  : null),
        ),
        child: (_newPhoto == null && _profile!['photoUrl'] == null)
            ? const Icon(Icons.person, color: Colors.white, size: 48)
            : null,
      ),
      if (widget.isOwner)
        Positioned(
          bottom: 0,
          right: 0,
          child: Container(
            padding: const EdgeInsets.all(6),
            decoration: const BoxDecoration(
              color: Color(0xFF7C3AED),
              shape: BoxShape.circle,
            ),
            child: const Icon(Icons.camera_alt, color: Colors.white, size: 14),
          ),
        ),
    ],
  ),
),
                const SizedBox(height: 12),
                // Nombre (no editable)
                Text(
                  _profile!['name'] ?? '',
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 6),
                // Username (editable)
                _editMode
                    ? SizedBox(
                  width: 200,
                  child: _editField(
                    controller: _usernameCtrl,
                    textAlign: TextAlign.center,
                    prefix: '@',
                  ),
                )
                    : Text(
                  '@${_profile!['username'] ?? ''}',
                  style: const TextStyle(color: Color(0xFF7C3AED)),
                ),
                const SizedBox(height: 4),
                // Edad (no editable)
                Text(
                  '${_profile!['age'] ?? ''} años',
                  style: const TextStyle(color: Colors.grey, fontSize: 13),
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),

          // ── Biografía ──
          _sectionTitle('Biografía'),
          const SizedBox(height: 8),
          _editMode
              ? _editField(
            controller: _bioCtrl,
            hint: 'Cuéntanos algo sobre ti...',
            maxLines: 4,
          )
              : (_bioCtrl.text.isNotEmpty
              ? Container(
            width: double.infinity,
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: const Color(0xFF1A1A2E),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Text(
              _bioCtrl.text,
              style: const TextStyle(color: Colors.white70, height: 1.5),
            ),
          )
              : _emptyField('Sin biografía')),
          const SizedBox(height: 16),

          // ── Estilo en llamada ──
          _sectionTitle('En llamada'),
          const SizedBox(height: 8),
          _editMode
              ? _editField(
            controller: _callStyleCtrl,
            hint: 'Cómo eres en llamada...',
            maxLines: 3,
          )
              : (_callStyleCtrl.text.isNotEmpty
              ? Container(
            width: double.infinity,
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: const Color(0xFF1A1A2E),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Row(
              children: [
                const Icon(Icons.headset_mic,
                    color: Color(0xFF7C3AED), size: 18),
                const SizedBox(width: 10),
                Expanded(
                  child: Text(
                    _callStyleCtrl.text,
                    style: const TextStyle(color: Colors.white70),
                  ),
                ),
              ],
            ),
          )
              : _emptyField('Sin descripción')),
          const SizedBox(height: 16),

          // ── Juegos principales ──
          _sectionTitle('Juegos principales'),
          const SizedBox(height: 8),
          ..._games.asMap().entries.map((e) => _itemRow(
            label: e.value,
            onDelete: _editMode
                ? () => setState(() => _games.removeAt(e.key))
                : null,
            icon: Icons.sports_esports,
          )),
          if (_editMode && _games.length < 4)
            _addItemRow(
              controller: _gameInputCtrl,
              hint: 'Añadir juego...',
              onAdd: () {
                final val = _gameInputCtrl.text.trim();
                if (val.isNotEmpty) {
                  setState(() {
                    _games.add(val);
                    _gameInputCtrl.clear();
                  });
                }
              },
            ),
          if (!_editMode && _games.isEmpty) _emptyField('Sin juegos'),
          const SizedBox(height: 16),

          // ── Plataformas ──
          _sectionTitle('Plataformas'),
          const SizedBox(height: 8),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              ..._platforms.asMap().entries.map((e) => _editMode
                  ? _chipDeletable(
                e.value,
                onDelete: () =>
                    setState(() => _platforms.removeAt(e.key)),
              )
                  : _chip(e.value)),
            ],
          ),
          if (_editMode)
            Padding(
              padding: const EdgeInsets.only(top: 8),
              child: _addItemRow(
                controller: _platformInputCtrl,
                hint: 'Añadir plataforma...',
                onAdd: () {
                  final val = _platformInputCtrl.text.trim();
                  if (val.isNotEmpty) {
                    setState(() {
                      _platforms.add(val);
                      _platformInputCtrl.clear();
                    });
                  }
                },
              ),
            ),
          if (!_editMode && _platforms.isEmpty) _emptyField('Sin plataformas'),
          const SizedBox(height: 16),

          // ── Idiomas ──
          _sectionTitle('Idiomas'),
          const SizedBox(height: 8),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              ..._languages.asMap().entries.map((e) => _editMode
                  ? _chipDeletable(
                e.value,
                onDelete: () =>
                    setState(() => _languages.removeAt(e.key)),
              )
                  : _chip(e.value)),
            ],
          ),
          if (_editMode)
            Padding(
              padding: const EdgeInsets.only(top: 8),
              child: _addItemRow(
                controller: _languageInputCtrl,
                hint: 'Añadir idioma...',
                onAdd: () {
                  final val = _languageInputCtrl.text.trim();
                  if (val.isNotEmpty) {
                    setState(() {
                      _languages.add(val);
                      _languageInputCtrl.clear();
                    });
                  }
                },
              ),
            ),
          if (!_editMode && _languages.isEmpty) _emptyField('Sin idiomas'),
          const SizedBox(height: 16),

          // ── Zona horaria ──
          _sectionTitle('Zona horaria'),
          const SizedBox(height: 8),
          _editMode
              ? _editField(
            controller: _timezoneCtrl,
            hint: 'Ej: Europe/Madrid',
          )
              : (_timezoneCtrl.text.isNotEmpty
              ? Container(
            width: double.infinity,
            padding: const EdgeInsets.symmetric(
                horizontal: 16, vertical: 12),
            decoration: BoxDecoration(
              color: const Color(0xFF1A1A2E),
              borderRadius: BorderRadius.circular(10),
            ),
            child: Row(
              children: [
                const Icon(Icons.access_time,
                    color: Color(0xFF7C3AED), size: 18),
                const SizedBox(width: 10),
                Text(_timezoneCtrl.text,
                    style: const TextStyle(color: Colors.white)),
              ],
            ),
          )
              : _emptyField('Sin zona horaria')),
          const SizedBox(height: 16),

          // ── País ──
          _sectionTitle('País'),
          const SizedBox(height: 8),
          _editMode
              ? _editField(
            controller: _countryCtrl,
            hint: 'Ej: España',
          )
              : (_countryCtrl.text.isNotEmpty
              ? Container(
            width: double.infinity,
            padding: const EdgeInsets.symmetric(
                horizontal: 16, vertical: 12),
            decoration: BoxDecoration(
              color: const Color(0xFF1A1A2E),
              borderRadius: BorderRadius.circular(10),
            ),
            child: Row(
              children: [
                const Icon(Icons.flag,
                    color: Color(0xFF7C3AED), size: 18),
                const SizedBox(width: 10),
                Text(_countryCtrl.text,
                    style: const TextStyle(color: Colors.white)),
              ],
            ),
          )
              : _emptyField('Sin país')),
          const SizedBox(height: 16),

          // ── Grupo actual ──
          if (widget.participant != null &&
              widget.participant!['group'] != null) ...[
            _sectionTitle('Grupo actual'),
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: const Color(0xFF1A1A2E),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        widget.participant!['group']['name'] ?? '',
                        style: const TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      Text(
                        widget.participant!['group']['game'] ?? '',
                        style: const TextStyle(color: Colors.grey),
                      ),
                    ],
                  ),
                  Container(
                    padding: const EdgeInsets.symmetric(
                        horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(
                      color: const Color(0xFF7C3AED).withOpacity(0.2),
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Text(
                      widget.participant!['group']['mode'] ?? '',
                      style: const TextStyle(
                          color: Color(0xFF7C3AED), fontSize: 12),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
          ],

          // ── Cerrar sesión ──
          if (widget.isOwner && !_editMode)
            SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                onPressed: () => Navigator.pushAndRemoveUntil(
                  context,
                  MaterialPageRoute(builder: (_) => const LoginScreen()),
                      (route) => false,
                ),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.red,
                  padding: const EdgeInsets.symmetric(vertical: 14),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
                icon: const Icon(Icons.exit_to_app, color: Colors.white),
                label: const Text(
                  'Cerrar Sesión',
                  style: TextStyle(color: Colors.white),
                ),
              ),
            ),
          const SizedBox(height: 24),
        ],
      ),
    );
  }

  // ── Widgets auxiliares ──

  Widget _sectionTitle(String title) {
    return Text(
      title,
      style: const TextStyle(
        color: Colors.white,
        fontSize: 15,
        fontWeight: FontWeight.bold,
      ),
    );
  }

  Widget _editField({
    required TextEditingController controller,
    String? hint,
    int maxLines = 1,
    TextAlign textAlign = TextAlign.start,
    String? prefix,
  }) {
    return TextField(
      controller: controller,
      maxLines: maxLines,
      textAlign: textAlign,
      style: const TextStyle(color: Colors.white),
      decoration: InputDecoration(
        prefixText: prefix,
        prefixStyle: const TextStyle(color: Color(0xFF7C3AED)),
        hintText: hint,
        hintStyle: const TextStyle(color: Colors.grey),
        filled: true,
        fillColor: const Color(0xFF1A1A2E),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10),
          borderSide: const BorderSide(color: Color(0xFF7C3AED), width: 1),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10),
          borderSide:
          BorderSide(color: const Color(0xFF7C3AED).withOpacity(0.3)),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10),
          borderSide: const BorderSide(color: Color(0xFF7C3AED)),
        ),
      ),
    );
  }

  Widget _addItemRow({
    required TextEditingController controller,
    required String hint,
    required VoidCallback onAdd,
  }) {
    return Row(
      children: [
        Expanded(
          child: TextField(
            controller: controller,
            style: const TextStyle(color: Colors.white),
            decoration: InputDecoration(
              hintText: hint,
              hintStyle: const TextStyle(color: Colors.grey),
              filled: true,
              fillColor: const Color(0xFF1A1A2E),
              isDense: true,
              contentPadding:
              const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(8),
                borderSide: BorderSide.none,
              ),
            ),
            onSubmitted: (_) => onAdd(),
          ),
        ),
        const SizedBox(width: 8),
        GestureDetector(
          onTap: onAdd,
          child: Container(
            padding: const EdgeInsets.all(10),
            decoration: BoxDecoration(
              color: const Color(0xFF7C3AED),
              borderRadius: BorderRadius.circular(8),
            ),
            child: const Icon(Icons.add, color: Colors.white, size: 18),
          ),
        ),
      ],
    );
  }

  Widget _itemRow({
    required String label,
    required IconData icon,
    VoidCallback? onDelete,
  }) {
    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      decoration: BoxDecoration(
        color: const Color(0xFF1A1A2E),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Row(
        children: [
          Icon(icon, color: const Color(0xFF7C3AED), size: 18),
          const SizedBox(width: 10),
          Expanded(
            child: Text(label, style: const TextStyle(color: Colors.white)),
          ),
          if (onDelete != null)
            GestureDetector(
              onTap: onDelete,
              child: const Icon(Icons.close, color: Colors.grey, size: 18),
            ),
        ],
      ),
    );
  }

  Widget _chip(String label) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: const Color(0xFF7C3AED).withOpacity(0.15),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(
            color: const Color(0xFF7C3AED).withOpacity(0.4)),
      ),
      child: Text(
        label,
        style: const TextStyle(color: Color(0xFF7C3AED), fontSize: 13),
      ),
    );
  }

  Widget _chipDeletable(String label, {required VoidCallback onDelete}) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
      decoration: BoxDecoration(
        color: const Color(0xFF7C3AED).withOpacity(0.15),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(
            color: const Color(0xFF7C3AED).withOpacity(0.4)),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            label,
            style: const TextStyle(color: Color(0xFF7C3AED), fontSize: 13),
          ),
          const SizedBox(width: 6),
          GestureDetector(
            onTap: onDelete,
            child: const Icon(Icons.close,
                color: Color(0xFF7C3AED), size: 14),
          ),
        ],
      ),
    );
  }

  Widget _emptyField(String text) {
    return Text(text, style: const TextStyle(color: Colors.grey, fontSize: 13));
  }

  Future<void> _pickAndUploadPhoto() async {
  final picker = ImagePicker();
  final picked = await picker.pickImage(
    source: ImageSource.gallery,
    maxWidth: 512,
    maxHeight: 512,
    imageQuality: 85,
  );
  if (picked == null) return;

  final file = File(picked.path);
  final url = await ApiService.uploadUserPhoto(widget.user['idUser'], file);

  if (url != null) {
    setState(() {
      _profile!['photoUrl'] = url;
      _newPhoto = file;
    });
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Foto actualizada'),
          backgroundColor: Color(0xFF7C3AED),
        ),
      );
    }
  } else {
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Error al subir la foto'),
          backgroundColor: Colors.red,
        ),
      );
    }
  }
}
}